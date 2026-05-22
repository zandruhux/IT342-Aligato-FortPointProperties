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
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.shared.exception.AppException;
import edu.cit.aligato.fortpointproperties.shared.validation.ImageUploadValidator;

@Service
public class PropertyPhotoStorageService {

    private static final long MAX_PROPERTY_PHOTO_SIZE_BYTES = 5 * 1024 * 1024;

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
                throw uploadFailed();
            }

            return new UploadedPropertyPhoto(storagePath, getPublicUrl(storagePath));
        } catch (IOException e) {
            throw uploadFailed();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw uploadFailed();
        }
    }

    private void validatePhoto(MultipartFile file) {
        ImageUploadValidator.validateRequiredImage(
                file,
                MAX_PROPERTY_PHOTO_SIZE_BYTES,
                "PROP-IMG-001",
                "Property image size exceeds maximum limit",
                "PROP-IMG-002",
                "Invalid property image type",
                "PROP-IMG-004",
                "Property image is required");
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
        return new AppException("PROP-IMG-003", "Property image upload failed", HttpStatus.BAD_REQUEST);
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
