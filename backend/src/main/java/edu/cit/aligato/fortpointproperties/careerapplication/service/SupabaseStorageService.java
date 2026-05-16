package edu.cit.aligato.fortpointproperties.careerapplication.service;

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
public class SupabaseStorageService {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final int signedUrlExpirationSeconds;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SupabaseStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey,
            @Value("${supabase.storage.bucket}") String bucket,
            @Value("${supabase.storage.signed-url-expiration-seconds}") int signedUrlExpirationSeconds) {
        this.supabaseUrl = trimTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.signedUrlExpirationSeconds = signedUrlExpirationSeconds;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public UploadedFile uploadCareerResume(String userId, MultipartFile file) {
        ensureConfigured();

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "resume"
                : file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String storagePath = "career-applications/" + userId + "/" + UUID.randomUUID() + "." + extension;

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
                throw new IllegalArgumentException("Failed to upload resume to Supabase Storage");
            }

            return new UploadedFile(storagePath, originalFilename);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read resume file for upload");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Resume upload was interrupted");
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
                throw new IllegalArgumentException("Failed to create signed resume URL");
            }

            Map<String, Object> responseBody = objectMapper.readValue(response.body(),
                    new TypeReference<Map<String, Object>>() {
                    });
            Object signedUrl = responseBody.get("signedURL");
            if (signedUrl == null) {
                signedUrl = responseBody.get("signedUrl");
            }
            if (signedUrl == null) {
                throw new IllegalArgumentException("Failed to create signed resume URL");
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
            throw new IllegalArgumentException("Failed to parse signed resume URL response");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Signed URL request was interrupted");
        }
    }

    private void ensureConfigured() {
        if (supabaseUrl == null || supabaseUrl.isBlank()
                || serviceRoleKey == null || serviceRoleKey.isBlank()
                || bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("Supabase Storage is not configured");
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

    public static class UploadedFile {
        private final String path;
        private final String originalFilename;

        public UploadedFile(String path, String originalFilename) {
            this.path = path;
            this.originalFilename = originalFilename;
        }

        public String getPath() {
            return path;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }
    }
}
