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

import edu.cit.aligato.fortpointproperties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyAgentController - Agents can view all properties with full details
 * Agents have access to: developerName, pitchReadyPhrases, developerLinks, priceComputations
 * Agents can also search and filter properties
 */
@RestController
@RequestMapping("/agent/properties")
@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public class PropertyAgentController {

    private final PropertyService propertyService;

    public PropertyAgentController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // Get all properties (Agent view - card view)
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
            ErrorDetail error = new ErrorDetail("PROP-010", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get property by ID (Agent view - advanced/detailed)
    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponse<PropertyDTO>> getPropertyByIdAdvanced(@PathVariable String id) {
        try {
            PropertyDTO property = propertyService.getPropertyById(id);
            ApiResponse<PropertyDTO> response = ApiResponse.success(property);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-011", "Property not found", null);
            ApiResponse<PropertyDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Search properties by name (Agent view)
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> searchByName(@RequestParam String name) {
        try {
            List<PropertyDTO> properties = propertyService.searchByName(name);
            ApiResponse<List<PropertyDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-012", e.getMessage(), null);
            ApiResponse<List<PropertyDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Filter properties by listing type (Agent view)
    @GetMapping("/filter/listing-type")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> filterByListingType(@RequestParam String listingType) {
        try {
            List<PropertyDTO> properties = propertyService.searchByListingType(listingType);
            ApiResponse<List<PropertyDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-013", e.getMessage(), null);
            ApiResponse<List<PropertyDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Get properties by developer name (Agent view)
    @GetMapping("/developer/{developerName}")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getPropertiesByDeveloper(@PathVariable String developerName) {
        try {
            List<PropertyDTO> properties = propertyService.getAllProperties().stream()
                    .filter(p -> p.getDeveloperName().equalsIgnoreCase(developerName))
                    .toList();

            ApiResponse<List<PropertyDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-014", e.getMessage(), null);
            ApiResponse<List<PropertyDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
