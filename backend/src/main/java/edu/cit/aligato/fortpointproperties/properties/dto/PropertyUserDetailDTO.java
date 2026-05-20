package edu.cit.aligato.fortpointproperties.properties.dto;

import java.util.ArrayList;
import java.util.List;

import edu.cit.aligato.fortpointproperties.properties.enums.FinancingType;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;

public class PropertyUserDetailDTO {
    public String id;
    public String name;
    public String basicDescription;
    public String location;
    public Double priceRangeMin;
    public Double priceRangeMax;
    public List<ListingType> listingTypes = new ArrayList<>();
    public List<FinancingType> financingTypes = new ArrayList<>();
    public Boolean petFriendly;
    public Boolean parkingAvailable;
    public Boolean hasPromo;
    public String turnoverDate;
    public List<AmenityDTO> amenities = new ArrayList<>();
    public List<PropertyPhotoDTO> photos = new ArrayList<>();
    public List<PropertyUnitDTO> units = new ArrayList<>();
}
