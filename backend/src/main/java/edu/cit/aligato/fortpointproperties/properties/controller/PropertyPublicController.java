package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;
import edu.cit.aligato.fortpointproperties.shared.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.shared.dto.ErrorDetail;

@RestController
@RequestMapping("/properties")
public class PropertyPublicController {

    private final PropertyService propertyService;

    public PropertyPublicController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPublicCards()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyCardDTO>> getPropertyById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.getPublicCardById(id)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-007", "Property not found", null)), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String developer,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(ApiResponse.success(
                propertyService.searchCards(name, location, developer, listingType, minPrice, maxPrice, false)));
    }

    @GetMapping("/search/location")
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> searchByLocation(@RequestParam String location) {
        return search(null, location, null, null, null, null);
    }
}
