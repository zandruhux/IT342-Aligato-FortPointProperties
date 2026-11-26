package edu.cit.aligato.fortpointproperties.article.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.shared.exception.AppException;

class ArticleStorageServiceTest {

    @Test
    void uploadCoverPhoto_nullOriginalFilename_doesNotThrowNullPointer() throws IOException {
        // Unconfigured storage makes the upload stop at the config check, after
        // the filename has been processed and before any network call. The service
        // maps that to its own AppException; an NPE would escape uncaught (500).
        ArticleStorageService service = new ArticleStorageService("", "", "", 300);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1}));

        AppException error = assertThrows(AppException.class, () -> service.uploadCoverPhoto(file));

        assertEquals("ART-IMG-003", error.getCode());
    }
}
