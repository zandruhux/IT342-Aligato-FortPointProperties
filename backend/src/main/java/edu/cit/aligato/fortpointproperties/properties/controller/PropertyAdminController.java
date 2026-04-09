package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.aligato.fortpointproperties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;
import edu.cit.aligato.fortpointproperties.repository.UserRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
public class PropertyAdminController {

    private final PropertyService propertyService;
    private final UserRepository userRepository;

    public PropertyAdminController(PropertyService propertyService, UserRepository userRepository) {
        this.propertyService = propertyService;
        this.userRepository = userRepository;
    }

    
    @PostMapping
    public ResponseEntity<ApiResponse<PropertyDTO>> createProperty(@Valid @RequestBody PropertyCreateRequest request) {
        try {
            // Get the authenticated user email from Spring Security
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

            //Saves to the Database
            Property property = propertyService.createProperty(request, currentUser);
            
            // Convert to DTO (includes createdBy information)
            PropertyDTO dto = propertyService.convertPropertyToDTO(property);

            ApiResponse<PropertyDTO> response = ApiResponse.success(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-001", e.getMessage(), null);
            ApiResponse<PropertyDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // ADMIN - GETS ALL PROPERTIES (card view)
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
            ErrorDetail error = new ErrorDetail("PROP-002", e.getMessage(), null);
            ApiResponse<List<PropertyBasicDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ADMIN - GET PROPERTY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDTO>> getPropertyById(@PathVariable String id) {
        try {
            PropertyDTO property = propertyService.getPropertyById(id);
            ApiResponse<PropertyDTO> response = ApiResponse.success(property);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-003", "Property not found", null);
            ApiResponse<PropertyDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // ADMIN - UPDATE PROPERTY
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDTO>> updateProperty(@PathVariable String id, 
                                                                   @Valid @RequestBody PropertyCreateRequest request) {
        try {
            Property property = propertyService.updateProperty(id, request);
            PropertyDTO dto = new PropertyDTO();
            dto.setId(property.getId());
            dto.setPropertyName(property.getPropertyName());
            dto.setDeveloperName(property.getDeveloperName());
            dto.setPriceRangeMin(property.getPriceRangeMin());
            dto.setPriceRangeMax(property.getPriceRangeMax());
            dto.setLocation(property.getLocation());
            dto.setListingType(property.getListingType());

            ApiResponse<PropertyDTO> response = ApiResponse.success(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-004", e.getMessage(), null);
            ApiResponse<PropertyDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // ADMIN - DELETE PROPERTY
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable String id) {
        try {
            propertyService.deleteProperty(id);
            ApiResponse<Void> response = ApiResponse.success(null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("PROP-005", "Property not found", null);
            ApiResponse<Void> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
