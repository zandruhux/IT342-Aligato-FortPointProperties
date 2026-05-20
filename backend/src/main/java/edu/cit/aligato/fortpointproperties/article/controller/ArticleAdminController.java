package edu.cit.aligato.fortpointproperties.article.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.article.dto.ArticleCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleUpdateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.service.ArticleService;
import edu.cit.aligato.fortpointproperties.properties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.properties.dto.ErrorDetail;

@RestController
@RequestMapping("/api/admin/articles")
public class ArticleAdminController {

    private final ArticleService articleService;

    public ArticleAdminController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ArticleDTO>> createArticle(@ModelAttribute ArticleCreateRequestDTO request) {
        try {
            return new ResponseEntity<>(ApiResponse.success(articleService.createArticle(request)), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return articleError("ARTICLE-400", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ArticleDTO>> updateArticle(
            @PathVariable String id,
            @ModelAttribute ArticleUpdateRequestDTO request) {
        try {
            return new ResponseEntity<>(ApiResponse.success(articleService.updateArticle(id, request)), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return articleError("ARTICLE-404", e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return articleError("ARTICLE-400", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable String id) {
        try {
            articleService.deleteArticle(id);
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return articleError("ARTICLE-404", e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> articleError(String code, String message, HttpStatus status) {
        ErrorDetail error = new ErrorDetail(code, message, null);
        return new ResponseEntity<>(ApiResponse.error(error), status);
    }
}
