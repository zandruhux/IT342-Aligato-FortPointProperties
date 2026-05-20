package edu.cit.aligato.fortpointproperties.article.dto;

public class ArticleCreateRequestDTO {
    private String title;
    private String description;

    public ArticleCreateRequestDTO() {
    }

    public ArticleCreateRequestDTO(String title, String description) {
        this.title = title;
        this.description = description;
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
}
