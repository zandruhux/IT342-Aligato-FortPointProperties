package edu.cit.aligato.fortpointproperties.article.dto;

import java.time.LocalDateTime;

public class ArticleCardDTO {
    private String id;
    private String title;
    private String shortDescription;
    private String coverPhotoUrl;
    private String authorName;
    private LocalDateTime latestDate;

    public ArticleCardDTO() {
    }

    public ArticleCardDTO(String id, String title, String shortDescription, String coverPhotoUrl,
            String authorName, LocalDateTime latestDate) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.coverPhotoUrl = coverPhotoUrl;
        this.authorName = authorName;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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

    public LocalDateTime getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(LocalDateTime latestDate) {
        this.latestDate = latestDate;
    }
}
