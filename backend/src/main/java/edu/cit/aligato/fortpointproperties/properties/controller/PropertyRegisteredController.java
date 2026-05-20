package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.properties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.properties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUserDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

@RestController
@RequestMapping("/user/properties")
@PreAuthorize("isAuthenticated()")
public class PropertyRegisteredController {

    private final PropertyService propertyService;

    public PropertyRegisteredController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPublicCards()));
    }

    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponse<PropertyUserDetailDTO>> getPropertyByIdAdvanced(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.getUserDetail(id)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-016", "Property not found", null)), HttpStatus.NOT_FOUND);
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

    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> searchByName(@RequestParam String name) {
        return search(name, null, null, null, null, null);
    }

    @GetMapping("/search/location")
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> searchByLocation(@RequestParam String location) {
        return search(null, location, null, null, null, null);
    }
}
