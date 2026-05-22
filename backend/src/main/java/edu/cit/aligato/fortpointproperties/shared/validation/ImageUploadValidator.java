package edu.cit.aligato.fortpointproperties.shared.validation;

import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.shared.exception.AppException;

public final class ImageUploadValidator {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp");

    private ImageUploadValidator() {
    }

    public static void validateRequiredImage(
            MultipartFile file,
            long maxSizeBytes,
            String sizeCode,
            String sizeMessage,
            String typeCode,
            String typeMessage,
            String emptyCode,
            String emptyMessage) {
        if (file == null || file.isEmpty()) {
            throw badRequest(emptyCode, emptyMessage);
        }

        if (file.getSize() > maxSizeBytes) {
            throw badRequest(sizeCode, sizeMessage);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))
                || !hasAllowedExtension(file.getOriginalFilename())) {
            throw badRequest(typeCode, typeMessage);
        }
    }

    private static boolean hasAllowedExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == filename.length() - 1) {
            return false;
        }

        String extension = filename.substring(extensionIndex + 1).toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private static AppException badRequest(String code, String message) {
        return new AppException(code, message, HttpStatus.BAD_REQUEST);
    }
}
