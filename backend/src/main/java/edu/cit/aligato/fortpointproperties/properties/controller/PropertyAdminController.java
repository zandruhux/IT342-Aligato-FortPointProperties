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
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;
import edu.cit.aligato.fortpointproperties.repository.UserRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/properties")
@PreAuthorize("hasRole('ADMIN')")
public class PropertyAdminController {

    private final PropertyService propertyService;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public PropertyAdminController(PropertyService propertyService, UserRepository userRepository,
                                    PropertyRepository propertyRepository) {
        this.propertyService = propertyService;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
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
                            dto.getName(),
                            dto.getBasicDescription(),
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
            PropertyDTO dto = propertyService.convertPropertyToDTO(property);

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

    // ========== UNIT MANAGEMENT ENDPOINTS ==========

    /**
     * Get all units for a property
     */
    @GetMapping("/{propertyId}/units")
    public ResponseEntity<ApiResponse<List<PropertyUnitDTO>>> getPropertyUnits(@PathVariable String propertyId) {
        try {
            List<PropertyUnitDTO> units = propertyService.getPropertyUnits(propertyId);
            ApiResponse<List<PropertyUnitDTO>> response = ApiResponse.success(units);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ErrorDetail error = new ErrorDetail("UNIT-001", e.getMessage(), null);
            ApiResponse<List<PropertyUnitDTO>> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Add a new unit to a property
     */
    @PostMapping("/{propertyId}/units")
    public ResponseEntity<ApiResponse<PropertyUnitDTO>> createPropertyUnit(
            @PathVariable String propertyId,
            @Valid @RequestBody PropertyUnitCreateRequest request) {
        try {
            var unit = propertyService.createPropertyUnit(propertyId, request);
            var dto = new PropertyUnitDTO();
            dto.setId(unit.getId());
            dto.setUnitType(unit.getUnitType());
            dto.setFloorArea(unit.getFloorArea());
            dto.setLotArea(unit.getLotArea());
            dto.setReservationFee(unit.getReservationFee());
            dto.setEquityPeriodMonths(unit.getEquityPeriodMonths());
            dto.setMonthlyEquity(unit.getMonthlyEquity());
            dto.setTotalSellingPrice(unit.getTotalSellingPrice());
            dto.setFinancingTypes(unit.getFinancingTypes());
            dto.setCreatedAt(unit.getCreatedAt());
            dto.setUpdatedAt(unit.getUpdatedAt());

            ApiResponse<PropertyUnitDTO> response = ApiResponse.success(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("UNIT-002", e.getMessage(), null);
            ApiResponse<PropertyUnitDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update a unit
     */
    @PutMapping("/{propertyId}/units/{unitId}")
    public ResponseEntity<ApiResponse<PropertyUnitDTO>> updatePropertyUnit(
            @PathVariable String propertyId,
            @PathVariable String unitId,
            @Valid @RequestBody PropertyUnitCreateRequest request) {
        try {
            var unit = propertyService.updatePropertyUnit(unitId, request);
            var dto = new PropertyUnitDTO();
            dto.setId(unit.getId());
            dto.setUnitType(unit.getUnitType());
            dto.setFloorArea(unit.getFloorArea());
            dto.setLotArea(unit.getLotArea());
            dto.setReservationFee(unit.getReservationFee());
            dto.setEquityPeriodMonths(unit.getEquityPeriodMonths());
            dto.setMonthlyEquity(unit.getMonthlyEquity());
            dto.setTotalSellingPrice(unit.getTotalSellingPrice());
            dto.setFinancingTypes(unit.getFinancingTypes());
            dto.setCreatedAt(unit.getCreatedAt());
            dto.setUpdatedAt(unit.getUpdatedAt());

            ApiResponse<PropertyUnitDTO> response = ApiResponse.success(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("UNIT-002", e.getMessage(), null);
            ApiResponse<PropertyUnitDTO> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a unit from a property
     */
    @DeleteMapping("/{propertyId}/units/{unitId}")
    public ResponseEntity<ApiResponse<Void>> deletePropertyUnit(
            @PathVariable String propertyId,
            @PathVariable String unitId) {
        try {
            propertyService.deletePropertyUnit(unitId);
            ApiResponse<Void> response = ApiResponse.success(null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ErrorDetail error = new ErrorDetail("UNIT-003", "Unit not found", null);
            ApiResponse<Void> errorResponse = ApiResponse.error(error);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


}
