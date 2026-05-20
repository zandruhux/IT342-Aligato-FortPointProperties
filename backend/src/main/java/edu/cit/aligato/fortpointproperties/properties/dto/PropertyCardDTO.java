package edu.cit.aligato.fortpointproperties.properties.dto;

import java.util.ArrayList;
import java.util.List;

import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;

public class PropertyCardDTO {
    public String id;
    public String name;
    public String basicDescription;
    public String location;
    public Double priceRangeMin;
    public Double priceRangeMax;
    public List<ListingType> listingTypes = new ArrayList<>();
    public Boolean hasPromo;
    public String coverPhotoUrl;
}
