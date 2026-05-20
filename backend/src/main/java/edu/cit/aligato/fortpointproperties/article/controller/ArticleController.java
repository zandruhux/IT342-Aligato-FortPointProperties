package edu.cit.aligato.fortpointproperties.article.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.article.dto.ArticleCardDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleDTO;
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

    private <T> ResponseEntity<ApiResponse<T>> articleError(String code, String message, HttpStatus status) {
        ErrorDetail error = new ErrorDetail(code, message, null);
        return new ResponseEntity<>(ApiResponse.error(error), status);
    }
}
