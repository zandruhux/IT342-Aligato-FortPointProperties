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
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyLimitedDTO;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

/**
 * PropertyRegisteredController - Registered (authenticated) users can view limited property details
 * Excludes: developer, keySellingPoints, brochurePdfUrl, inventoryLink
 * Includes: all other property details, search, and filter capabilities
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

    //Get property by ID (Registered users view - advanced/detailed)
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

    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<PropertyLimitedDTO>>> searchByName(@RequestParam String name) {
        try {
            List<PropertyLimitedDTO> properties = propertyService.searchByName(name).stream()
                    .map(this::convertToLimitedDTO)
                    .toList();

            ApiResponse<List<PropertyLimitedDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-017", e.getMessage(), null);
            ApiResponse<List<PropertyLimitedDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search/location")
    public ResponseEntity<ApiResponse<List<PropertyLimitedDTO>>> searchByLocation(@RequestParam String location) {
        try {
            List<PropertyLimitedDTO> properties = propertyService.searchByLocation(location).stream()
                    .map(this::convertToLimitedDTO)
                    .toList();

            ApiResponse<List<PropertyLimitedDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-018", e.getMessage(), null);
            ApiResponse<List<PropertyLimitedDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/filter/listing-type")
    public ResponseEntity<ApiResponse<List<PropertyLimitedDTO>>> filterByListingType(@RequestParam String listingType) {
        try {
            List<PropertyLimitedDTO> properties = propertyService.searchByListingType(listingType).stream()
                    .map(this::convertToLimitedDTO)
                    .toList();

            ApiResponse<List<PropertyLimitedDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-019", e.getMessage(), null);
            ApiResponse<List<PropertyLimitedDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/filter/pet-friendly")
    public ResponseEntity<ApiResponse<List<PropertyLimitedDTO>>> getPetFriendly() {
        try {
            List<PropertyLimitedDTO> properties = propertyService.getPetFriendlyProperties().stream()
                    .map(this::convertToLimitedDTO)
                    .toList();

            ApiResponse<List<PropertyLimitedDTO>> response = ApiResponse.success(properties);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("PROP-020", e.getMessage(), null);
            ApiResponse<List<PropertyLimitedDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
