package edu.cit.aligato.fortpointproperties.article.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ArticleStorageService {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final int signedUrlExpirationSeconds;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ArticleStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey,
            @Value("${supabase.article-bucket}") String bucket,
            @Value("${supabase.storage.signed-url-expiration-seconds:300}") int signedUrlExpirationSeconds) {
        this.supabaseUrl = trimTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.signedUrlExpirationSeconds = signedUrlExpirationSeconds;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public UploadedArticlePhoto uploadCoverPhoto(MultipartFile file) {
        ensureConfigured();

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "cover-photo"
                : file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String storagePath = "articles/" + UUID.randomUUID() + "." + extension;

        try (InputStream inputStream = file.getInputStream()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/" + encodePath(bucket) + "/"
                            + encodePath(storagePath)))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", file.getContentType())
                    .header("x-upsert", "false")
                    .POST(HttpRequest.BodyPublishers.ofInputStream(() -> inputStream))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalArgumentException("Failed to upload article cover photo");
            }

            return new UploadedArticlePhoto(storagePath, getPublicUrl(storagePath));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read article cover photo for upload");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Article cover photo upload was interrupted");
        }
    }

    public String createSignedUrl(String storagePath) {
        ensureConfigured();

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "expiresIn", signedUrlExpirationSeconds));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/sign/" + encodePath(bucket) + "/"
                            + encodePath(storagePath)))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return getPublicUrl(storagePath);
            }

            Map<String, Object> responseBody = objectMapper.readValue(response.body(),
                    new TypeReference<Map<String, Object>>() {
                    });
            Object signedUrl = responseBody.get("signedURL");
            if (signedUrl == null) {
                signedUrl = responseBody.get("signedUrl");
            }
            if (signedUrl == null) {
                return getPublicUrl(storagePath);
            }

            String signedUrlValue = signedUrl.toString();
            if (signedUrlValue.startsWith("http")) {
                return signedUrlValue;
            }
            if (signedUrlValue.startsWith("/storage/v1")) {
                return supabaseUrl + signedUrlValue;
            }
            if (signedUrlValue.startsWith("/object")) {
                return supabaseUrl + "/storage/v1" + signedUrlValue;
            }
            return supabaseUrl + signedUrlValue;
        } catch (IOException e) {
            return getPublicUrl(storagePath);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return getPublicUrl(storagePath);
        }
    }

    public void deleteCoverPhoto(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return;
        }
        ensureConfigured();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/storage/v1/object/" + encodePath(bucket) + "/"
                            + encodePath(storagePath)))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to delete article cover photo");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Article cover photo deletion was interrupted");
        }
    }

    private String getPublicUrl(String storagePath) {
        return supabaseUrl + "/storage/v1/object/public/" + encodePath(bucket) + "/" + encodePath(storagePath);
    }

    private void ensureConfigured() {
        if (supabaseUrl == null || supabaseUrl.isBlank()
                || serviceRoleKey == null || serviceRoleKey.isBlank()
                || bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("Supabase Article Storage is not configured");
        }
    }

    private String getFileExtension(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == filename.length() - 1) {
            return "bin";
        }
        return filename.substring(extensionIndex + 1).toLowerCase();
    }

    private String encodePath(String path) {
        return Arrays.stream(path.split("/"))
                .map(segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    public static class UploadedArticlePhoto {
        private final String path;
        private final String url;

        public UploadedArticlePhoto(String path, String url) {
            this.path = path;
            this.url = url;
        }

        public String getPath() {
            return path;
        }

        public String getUrl() {
            return url;
        }
    }
}
