package edu.cit.aligato.fortpointproperties.article.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.article.dto.ArticleCardDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleDTO;
import edu.cit.aligato.fortpointproperties.article.dto.ArticleUpdateRequestDTO;
import edu.cit.aligato.fortpointproperties.article.entity.Article;
import edu.cit.aligato.fortpointproperties.article.repository.ArticleRepository;
import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;

@Service
public class ArticleService {

    private static final int CARD_DESCRIPTION_LIMIT = 110;

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleStorageService articleStorageService;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository,
            ArticleStorageService articleStorageService) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.articleStorageService = articleStorageService;
    }

    public ArticleDTO createArticle(ArticleCreateRequestDTO request) {
        MultipartFile coverPhoto = request.getCoverPhoto();
        String authorEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        validateRequiredText(request.getTitle(), "Title is required");
        validateRequiredText(request.getDescription(), "Description is required");
        validateRequiredCoverPhoto(coverPhoto);
        validateImageFile(coverPhoto);

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated admin not found"));
        ArticleStorageService.UploadedArticlePhoto uploadedPhoto = articleStorageService.uploadCoverPhoto(coverPhoto);

        Article article = new Article();
        article.setTitle(request.getTitle().trim());
        article.setDescription(request.getDescription().trim());
        article.setCoverPhotoUrl(uploadedPhoto.getUrl());
        article.setCoverPhotoPath(uploadedPhoto.getPath());
        article.setAuthorId(author.getId());
        article.setAuthorName(buildAuthorName(author));

        return convertArticleToDTO(articleRepository.save(article));
    }

    public ArticleDTO updateArticle(String id, ArticleUpdateRequestDTO request) {
        MultipartFile coverPhoto = request.getCoverPhoto();

        validateRequiredText(request.getTitle(), "Title is required");
        validateRequiredText(request.getDescription(), "Description is required");

        Article article = getArticleEntity(id);
        article.setTitle(request.getTitle().trim());
        article.setDescription(request.getDescription().trim());

        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            validateImageFile(coverPhoto);
            String oldCoverPhotoPath = article.getCoverPhotoPath();
            ArticleStorageService.UploadedArticlePhoto uploadedPhoto = articleStorageService.uploadCoverPhoto(coverPhoto);
            article.setCoverPhotoUrl(uploadedPhoto.getUrl());
            article.setCoverPhotoPath(uploadedPhoto.getPath());
            deleteCoverPhotoIfPossible(oldCoverPhotoPath);
        }

        return convertArticleToDTO(articleRepository.save(article));
    }

    public void deleteArticle(String id) {
        Article article = getArticleEntity(id);
        articleRepository.delete(article);
        deleteCoverPhotoIfPossible(article.getCoverPhotoPath());
    }

    public List<ArticleCardDTO> getAllArticleCards() {
        return articleRepository.findAllOrderByLatestDateDesc().stream()
                .map(this::convertArticleToCardDTO)
                .toList();
    }

    public ArticleDTO getArticleById(String id) {
        return convertArticleToDTO(getArticleEntity(id));
    }

    private ArticleDTO convertArticleToDTO(Article article) {
        return new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                getDisplayCoverPhotoUrl(article),
                article.getAuthorName(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                getLatestDate(article));
    }

    private ArticleCardDTO convertArticleToCardDTO(Article article) {
        return new ArticleCardDTO(
                article.getId(),
                article.getTitle(),
                generateShortDescription(article.getDescription()),
                getDisplayCoverPhotoUrl(article),
                article.getAuthorName(),
                getLatestDate(article));
    }

    private Article getArticleEntity(String id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Article not found"));
    }

    private String generateShortDescription(String description) {
        if (description == null) {
            return "";
        }
        String normalizedDescription = description.trim().replaceAll("\\s+", " ");
        if (normalizedDescription.length() <= CARD_DESCRIPTION_LIMIT) {
            return normalizedDescription;
        }
        return normalizedDescription.substring(0, CARD_DESCRIPTION_LIMIT).trim() + "...";
    }

    private LocalDateTime getLatestDate(Article article) {
        return article.getUpdatedAt() != null ? article.getUpdatedAt() : article.getCreatedAt();
    }

    private String getDisplayCoverPhotoUrl(Article article) {
        if (article.getCoverPhotoPath() == null || article.getCoverPhotoPath().isBlank()) {
            return article.getCoverPhotoUrl();
        }
        try {
            return articleStorageService.createSignedUrl(article.getCoverPhotoPath());
        } catch (IllegalArgumentException e) {
            return article.getCoverPhotoUrl();
        }
    }

    private void validateRequiredText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateRequiredCoverPhoto(MultipartFile coverPhoto) {
        if (coverPhoto == null || coverPhoto.isEmpty()) {
            throw new IllegalArgumentException("Cover photo is required");
        }
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("Cover photo must be an image file");
        }
    }

    private String buildAuthorName(User user) {
        String firstName = user.getFirstname() == null ? "" : user.getFirstname().trim();
        String lastName = user.getLastname() == null ? "" : user.getLastname().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isEmpty() ? user.getEmail() : fullName;
    }

    private void deleteCoverPhotoIfPossible(String storagePath) {
        try {
            articleStorageService.deleteCoverPhoto(storagePath);
        } catch (IllegalArgumentException ignored) {
            // Keep article operations successful even if a stale storage object cannot be removed.
        }
    }
}
