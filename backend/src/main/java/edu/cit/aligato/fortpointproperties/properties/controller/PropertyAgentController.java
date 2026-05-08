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
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyAgentController - Agents can view all properties with full details
 * Agents have access to: developer, keySellingPoints, brochurePdfUrl, inventoryLink
 * 
 * Search and filtering is handled client-side by the frontend using applySearchFilters()
 * This controller provides full data retrieval for agents
 */
@RestController
@RequestMapping("/agent/properties")
@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public class PropertyAgentController {

    private final PropertyService propertyService;

    public PropertyAgentController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * Get all properties (Agent view - card view)
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
            ErrorDetail error = new ErrorDetail("PROP-010", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get property by ID (Agent view - advanced/detailed)
     */
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

    /**
     * Search properties by name (Agent view)
     * Backend search endpoint for scalability with large datasets
     */
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> searchByName(@RequestParam String name) {
        try {
            List<PropertyBasicDTO> properties = propertyService.getAllProperties().stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
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
            ErrorDetail error = new ErrorDetail("PROP-012", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search properties by location (Agent view)
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
            ErrorDetail error = new ErrorDetail("PROP-013", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search properties by developer (Agent view)
     * Backend search endpoint for scalability with large datasets
     */
    @GetMapping("/search/developer")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> searchByDeveloper(@RequestParam String developer) {
        try {
            List<PropertyBasicDTO> properties = propertyService.getAllProperties().stream()
                    .filter(p -> p.getDeveloper() != null && p.getDeveloper().toLowerCase().contains(developer.toLowerCase()))
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
            ErrorDetail error = new ErrorDetail("PROP-014", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Combined search endpoint supporting optional filters: name, location, developer, minPrice, maxPrice
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PropertyBasicDTO>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String developer,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        try {
            List<PropertyBasicDTO> properties = propertyService.searchWithFilters(name, location, developer, minPrice, maxPrice).stream()
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
            ErrorDetail error = new ErrorDetail("PROP-015", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
