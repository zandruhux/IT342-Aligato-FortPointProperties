package edu.cit.aligato.fortpointproperties.article.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.article.dto.ArticleCardDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleUpdateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.service.ArticleService;
import edu.cit.aligato.fortpointproperties.properties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.properties.dto.ErrorDetail;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<ArticleCardDTO>>> getPublicArticleCards() {
        ApiResponse<List<ArticleCardDTO>> response = ApiResponse.success(articleService.getAllArticleCards());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ArticleCardDTO>>> getArticleCards() {
        ApiResponse<List<ArticleCardDTO>> response = ApiResponse.success(articleService.getAllArticleCards());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> getArticleById(@PathVariable String id) {
        try {
            ApiResponse<ArticleDTO> response = ApiResponse.success(articleService.getArticleById(id));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return articleError("ARTICLE-404", e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ArticleDTO>> createArticle(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("coverPhoto") MultipartFile coverPhoto) {
        try {
            String authorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            ArticleCreateRequestDTO request = new ArticleCreateRequestDTO(title, description);
            ApiResponse<ArticleDTO> response = ApiResponse.success(
                    articleService.createArticle(request, coverPhoto, authorEmail));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return articleError("ARTICLE-400", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDTO>> updateArticle(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto) {
        try {
            ArticleUpdateRequestDTO request = new ArticleUpdateRequestDTO(title, description);
            ApiResponse<ArticleDTO> response = ApiResponse.success(
                    articleService.updateArticle(id, request, coverPhoto));
            return new ResponseEntity<>(response, HttpStatus.OK);
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
