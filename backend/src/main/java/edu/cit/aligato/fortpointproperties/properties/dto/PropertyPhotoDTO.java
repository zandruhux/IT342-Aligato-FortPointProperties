package edu.cit.aligato.fortpointproperties.properties.dto;

public class PropertyPhotoDTO {
    public String photoUrl;
    public Integer displayOrder;

    public PropertyPhotoDTO() {
    }

    public PropertyPhotoDTO(String photoUrl, Integer displayOrder) {
        this.photoUrl = photoUrl;
        this.displayOrder = displayOrder;
    }
}
