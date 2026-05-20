package edu.cit.aligato.fortpointproperties.properties.dto;

public class AmenityDTO {
    public String id;
    public String name;
    public Boolean defaultAmenity;
    public Long usageCount;

    public AmenityDTO() {
    }

    public AmenityDTO(String id, String name, Boolean defaultAmenity) {
        this(id, name, defaultAmenity, 0L);
    }

    public AmenityDTO(String id, String name, Boolean defaultAmenity, Long usageCount) {
        this.id = id;
        this.name = name;
        this.defaultAmenity = defaultAmenity;
        this.usageCount = usageCount != null ? usageCount : 0L;
    }
}
