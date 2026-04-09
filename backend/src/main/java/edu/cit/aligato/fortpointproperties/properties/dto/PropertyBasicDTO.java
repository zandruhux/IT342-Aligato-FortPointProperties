package edu.cit.aligato.fortpointproperties.properties.dto;

/**
 * PropertyBasicDTO - For PUBLIC users (unauthenticated)
 * Limited to: propertyName, description, location, priceRangeMin, priceRangeMax
 */
public class PropertyBasicDTO {
    private String id;
    private String propertyName;
    private String description;
    private String location;
    private Double priceRangeMin;
    private Double priceRangeMax;

    // --- Constructors ---
    public PropertyBasicDTO() {
    }

    public PropertyBasicDTO(String id, String propertyName, String description, String location, 
                           Double priceRangeMin, Double priceRangeMax) {
        this.id = id;
        this.propertyName = propertyName;
        this.description = description;
        this.location = location;
        this.priceRangeMin = priceRangeMin;
        this.priceRangeMax = priceRangeMax;
    }

    // --- Getters and Setters ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPriceRangeMin() {
        return priceRangeMin;
    }

    public void setPriceRangeMin(Double priceRangeMin) {
        this.priceRangeMin = priceRangeMin;
    }

    public Double getPriceRangeMax() {
        return priceRangeMax;
    }

    public void setPriceRangeMax(Double priceRangeMax) {
        this.priceRangeMax = priceRangeMax;
    }
}
