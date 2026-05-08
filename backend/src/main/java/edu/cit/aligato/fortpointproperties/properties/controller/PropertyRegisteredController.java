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
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyLimitedDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyRegisteredController - Registered (authenticated) users can view limited property details
 * 
 * Visible Fields:
 * - Includes: all common property details
 * - Excludes: developer, keySellingPoints, brochurePdfUrl, inventoryLink
 * 
 * Available Endpoints:
 * - Retrieve all properties (limited fields)
 * - Retrieve property details by ID (limited fields)
 * 
 * Search and Filtering:
 * - Clients retrieve full property list and perform filtering using applySearchFilters()
 * - No backend search endpoints - filtering is handled client-side
 * - This follows the simplified API design pattern
 */
@RestController
@RequestMapping("/user/properties")
@PreAuthorize("isAuthenticated()")
public class PropertyRegisteredController {

    private final PropertyService propertyService;

    public PropertyRegisteredController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    //Get all properties (Registered users view - card view)
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
            ErrorDetail error = new ErrorDetail("PROP-015", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/advanced")
    public ResponseEntity<ApiResponse<PropertyLimitedDTO>> getPropertyByIdAdvanced(@PathVariable String id) {
        try {
            PropertyLimitedDTO property = propertyService.getAllProperties().stream()
                    .filter(p -> p.getId().equals(id))
                    .map(this::convertToLimitedDTO)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Property not found"));

            ApiResponse<PropertyLimitedDTO> response = ApiResponse.success(property);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-016", "Property not found", null);
            ApiResponse<PropertyLimitedDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Search properties by name (Registered users)
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
            ErrorDetail error = new ErrorDetail("PROP-017", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search properties by location (Registered users)
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
            ErrorDetail error = new ErrorDetail("PROP-018", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Combined search endpoint for registered users supporting optional filters
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
            ErrorDetail error = new ErrorDetail("PROP-023", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Convert PropertyDTO to PropertyLimitedDTO
    // Excludes: developer, keySellingPoints, brochurePdfUrl, inventoryLink
    // Includes: units (read-only for registered users)
    private PropertyLimitedDTO convertToLimitedDTO(PropertyDTO dto) {
        PropertyLimitedDTO limited = new PropertyLimitedDTO();
        limited.setId(dto.getId());
        limited.setName(dto.getName());
        limited.setBasicDescription(dto.getBasicDescription());
        limited.setPriceRangeMin(dto.getPriceRangeMin());
        limited.setPriceRangeMax(dto.getPriceRangeMax());
        limited.setLocation(dto.getLocation());
        limited.setListingType(dto.getListingType());
        limited.setPetFriendly(dto.getPetFriendly());
        limited.setParkingAvailable(dto.getParkingAvailable());
        limited.setTurnoverDate(dto.getTurnoverDate());
        limited.setAmenities(dto.getAmenities());
        limited.setCreatedAt(dto.getCreatedAt());
        limited.setUpdatedAt(dto.getUpdatedAt());
        limited.setUnits(dto.getUnits());
        return limited;
    }
}
