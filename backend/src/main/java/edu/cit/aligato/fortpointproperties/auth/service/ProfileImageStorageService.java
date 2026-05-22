package edu.cit.aligato.fortpointproperties.auth.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.shared.exception.AppException;

@Service
public class ProfileImageStorageService {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final HttpClient httpClient;

    public ProfileImageStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey,
            @Value("${supabase.profile-image-bucket:profile-images}") String bucket) {
        this.supabaseUrl = trimTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.httpClient = HttpClient.newHttpClient();
    }

    public UploadedProfileImage uploadProfileImage(String userId, MultipartFile file) {
        ensureConfigured();

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "profile-image"
                : file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        // Store paths per user so old profile images can be replaced or removed safely.
        String storagePath = "profile-images/" + userId + "/" + UUID.randomUUID() + "." + extension;

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
                throw uploadFailed();
            }

            return new UploadedProfileImage(storagePath, getPublicUrl(storagePath));
        } catch (IOException e) {
            throw uploadFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uploadFailed();
        }
    }

    public void deleteProfileImage(String storagePath) {
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
            throw new IllegalArgumentException("Failed to delete profile image");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Profile image deletion was interrupted");
        }
    }

    private String getPublicUrl(String storagePath) {
        return supabaseUrl + "/storage/v1/object/public/" + encodePath(bucket) + "/" + encodePath(storagePath);
    }

    private void ensureConfigured() {
        if (supabaseUrl == null || supabaseUrl.isBlank()
            || serviceRoleKey == null || serviceRoleKey.isBlank()
                || bucket == null || bucket.isBlank()) {
            throw uploadFailed();
        }
    }

    private AppException uploadFailed() {
        return new AppException("AUTH-IMG-003", "Profile image upload failed", HttpStatus.BAD_REQUEST);
    }

    private String getFileExtension(String filename) {
        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == filename.length() - 1) {
            return "bin";
        }
        return filename.substring(extensionIndex + 1).toLowerCase();
    }

    private String encodePath(String path) {
        // Supabase object paths need segment-level encoding so folder separators remain intact.
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

    public static class UploadedProfileImage {
        private final String path;
        private final String url;

        public UploadedProfileImage(String path, String url) {
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
