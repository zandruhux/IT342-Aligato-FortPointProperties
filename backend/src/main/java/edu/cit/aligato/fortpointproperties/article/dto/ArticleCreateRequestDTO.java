package edu.cit.aligato.fortpointproperties.article.dto;

import org.springframework.web.multipart.MultipartFile;

public class ArticleCreateRequestDTO {
    private String title;
    private String description;
    private MultipartFile coverPhoto;

    public ArticleCreateRequestDTO() {
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

    public MultipartFile getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(MultipartFile coverPhoto) {
        this.coverPhoto = coverPhoto;
    }
}
