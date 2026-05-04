package edu.cit.aligato.fortpointproperties.properties.dto;

/**
 * PropertyBasicDTO - For PUBLIC users (unauthenticated)
 * Limited to: name, basicDescription, location, priceRangeMin, priceRangeMax
 */
public class PropertyBasicDTO {
    private String id;
    private String name;
    private String basicDescription;
    private String location;
    private Double priceRangeMin;
    private Double priceRangeMax;

    // --- Constructors ---
    public PropertyBasicDTO() {
    }

    public PropertyBasicDTO(String id, String name, String basicDescription, String location, 
                           Double priceRangeMin, Double priceRangeMax) {
        this.id = id;
        this.name = name;
        this.basicDescription = basicDescription;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBasicDescription() {
        return basicDescription;
    }

    public void setBasicDescription(String basicDescription) {
        this.basicDescription = basicDescription;
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
