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

import edu.cit.aligato.fortpointproperties.properties.dto.PropertyAgentDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;
import edu.cit.aligato.fortpointproperties.shared.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.shared.dto.ErrorDetail;

@RestController
@RequestMapping("/agent/properties")
@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public class PropertyAgentController {

    private final PropertyService propertyService;

    public PropertyAgentController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPublicCards()));
    }

    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponse<PropertyAgentDetailDTO>> getPropertyByIdAdvanced(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.getAgentDetail(id)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-011", "Property not found", null)), HttpStatus.NOT_FOUND);
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

    @GetMapping("/search/developer")
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> searchByDeveloper(@RequestParam String developer) {
        return search(null, null, developer, null, null, null);
    }
}
