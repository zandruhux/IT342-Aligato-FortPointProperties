package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyPublicController - Public (unauthenticated) users can view limited property details
 * Limited to: propertyName, description, location, priceRangeMin, priceRangeMax
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
     * Limited fields: propertyName, description, location, priceRangeMin, priceRangeMax
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> getAllProperties() {
        try {
            List<PropertyBasicDTO> properties = propertyService.getAllProperties().stream()
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getPropertyName(),
                            dto.getDescription(),
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
                            dto.getPropertyName(),
                            dto.getDescription(),
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
     */
    @GetMapping("/search/location")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> searchByLocation(@RequestParam String location) {
        try {
            List<PropertyBasicDTO> properties = propertyService.searchByLocation(location).stream()
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getPropertyName(),
                            dto.getDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .toList();

            ApiResponse<List<PropertyBasicDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-008", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get pet-friendly properties (Public users)
     */
    @GetMapping("/filter/pet-friendly")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> getPetFriendly() {
        try {
            List<PropertyBasicDTO> properties = propertyService.getPetFriendlyProperties().stream()
                    .map(dto -> new PropertyBasicDTO(
                            dto.getId(),
                            dto.getPropertyName(),
                            dto.getDescription(),
                            dto.getLocation(),
                            dto.getPriceRangeMin(),
                            dto.getPriceRangeMax()))
                    .toList();

            ApiResponse<List<PropertyBasicDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-009", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
