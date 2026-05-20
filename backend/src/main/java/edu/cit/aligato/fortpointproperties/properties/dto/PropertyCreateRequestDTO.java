package edu.cit.aligato.fortpointproperties.properties.dto;

import java.util.ArrayList;
import java.util.List;

import edu.cit.aligato.fortpointproperties.properties.enums.FinancingType;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class PropertyCreateRequestDTO {
    @NotBlank(message = "Property name is required")
    public String name;

    public String basicDescription;

    @NotBlank(message = "Developer name is required")
    public String developer;

    @NotBlank(message = "Location is required")
    public String location;

    @NotEmpty(message = "At least one listing type is required")
    public List<ListingType> listingTypes = new ArrayList<>();

    public List<FinancingType> financingTypes = new ArrayList<>();

    public Boolean petFriendly = false;
    public Boolean parkingAvailable = false;
    public Boolean hasPromo = false;
    public Boolean featured = false;
    public Boolean visible = true;

    @NotBlank(message = "Turnover date is required")
    public String turnoverDate;

    public List<String> amenityIds = new ArrayList<>();
    public List<String> customAmenities = new ArrayList<>();
    public String keySellingPoints;
    public String brochurePdfUrl;
    public String inventoryLink;

    @Valid
    public List<PropertyUnitRequestDTO> units;

    @Valid
    public List<PropertyPhotoDTO> photos = new ArrayList<>();
}
