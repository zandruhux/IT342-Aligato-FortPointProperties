package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.properties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.properties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyPublicController - Public (unauthenticated) users can view limited property details
 * Limited to: name, basicDescription, location, priceRangeMin, priceRangeMax
 * 
 * Search and filtering is handled client-side by the frontend using applySearchFilters()
 * This controller provides full data retrieval for the public audience
 */
@RestController
@RequestMapping("/properties")
public class PropertyPublicController {

    private final PropertyService propertyService;

    public PropertyPublicController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * Get all properties (Public users)
     * Limited fields: name, basicDescription, location, priceRangeMin, priceRangeMax
     * Clients perform filtering using applySearchFilters() on frontend
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> getAllProperties() {
        try {
            List<PropertyBasicDTO> properties = propertyService.getAllProperties().stream()
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getName(),
                            dto.getBasicDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .toList();

            ApiResponse<List<PropertyBasicDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-006", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get property by ID (Public users - basic view)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyBasicDTO>> getPropertyById(@PathVariable String id) {
        try {
            PropertyBasicDTO property = propertyService.getAllProperties().stream()
                    .filter(p -> p.getId().equals(id))
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getName(),
                            dto.getBasicDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            ApiResponse<PropertyBasicDTO> response = ApiResponse.success(property);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-007", "Property not found", null);
            ApiResponse<PropertyBasicDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Search properties by location (Public users)
     * Backend search endpoint for scalability with large datasets
     */
    @GetMapping("/search/location")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> searchByLocation(@RequestParam String location) {
        try {
            List<PropertyBasicDTO> properties = propertyService.getAllProperties().stream()
                    .filter(p -> p.getLocation() != null && p.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getName(),
                            dto.getBasicDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .toList();

            ApiResponse<List<PropertyBasicDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-008", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Combined search endpoint for public users supporting optional filters
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        try {
            List<PropertyBasicDTO> properties = propertyService.searchWithFilters(name, location, null, minPrice, maxPrice).stream()
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getName(),
                            dto.getBasicDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .toList();

            ApiResponse<List<PropertyBasicDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-024", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
