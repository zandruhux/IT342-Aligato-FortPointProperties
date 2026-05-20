package edu.cit.aligato.fortpointproperties.properties.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PropertyPhotoStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;
    private final HttpClient httpClient;

    public PropertyPhotoStorageService(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-role-key}") String serviceRoleKey,
            @Value("${supabase.property-photo-bucket:property-photos}") String bucket) {
        this.supabaseUrl = trimTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.httpClient = HttpClient.newHttpClient();
    }

    public UploadedPropertyPhoto uploadPhoto(MultipartFile file) {
        ensureConfigured();
        validatePhoto(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null
                ? "property-photo"
                : file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String storagePath = "properties/" + UUID.randomUUID() + "." + extension;

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
                throw new IllegalArgumentException("Failed to upload property photo (Supabase returned " + response.statusCode() + ")");
            }

            return new UploadedPropertyPhoto(storagePath, getPublicUrl(storagePath));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read property photo for upload");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Property photo upload was interrupted");
        }
    }

    private void validatePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Property photo is required");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Property photo must be JPG, PNG, or WEBP");
        }
    }

    private String getPublicUrl(String storagePath) {
        return supabaseUrl + "/storage/v1/object/public/" + encodePath(bucket) + "/" + encodePath(storagePath);
    }

    private void ensureConfigured() {
        if (supabaseUrl == null || supabaseUrl.isBlank()
                || serviceRoleKey == null || serviceRoleKey.isBlank()
                || bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("Supabase Property Photo Storage is not configured");
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

    public static class UploadedPropertyPhoto {
        private final String path;
        private final String url;

        public UploadedPropertyPhoto(String path, String url) {
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
