package edu.cit.aligato.fortpointproperties.article.dto;

import java.time.LocalDateTime;

public class ArticleDTO {
    private String id;
    private String title;
    private String description;
    private String coverPhotoUrl;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime latestDate;

    public ArticleDTO() {
    }

    public ArticleDTO(String id, String title, String description, String coverPhotoUrl, String authorName,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime latestDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.coverPhotoUrl = coverPhotoUrl;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latestDate = latestDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(LocalDateTime latestDate) {
        this.latestDate = latestDate;
    }
}
