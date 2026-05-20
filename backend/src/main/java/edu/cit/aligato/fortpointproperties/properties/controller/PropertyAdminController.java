package edu.cit.aligato.fortpointproperties.properties.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.auth.repository.UserRepository;
import edu.cit.aligato.fortpointproperties.properties.dto.AmenityDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.ApiResponse;
import edu.cit.aligato.fortpointproperties.properties.dto.ErrorDetail;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyAdminDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyPhotoDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUpdateRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyPhotoStorageService;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/properties")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
public class PropertyAdminController {

    private final PropertyService propertyService;
    private final PropertyPhotoStorageService propertyPhotoStorageService;
    private final UserRepository userRepository;

    public PropertyAdminController(
            PropertyService propertyService,
            PropertyPhotoStorageService propertyPhotoStorageService,
            UserRepository userRepository) {
        this.propertyService = propertyService;
        this.propertyPhotoStorageService = propertyPhotoStorageService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PropertyAdminDetailDTO>> createProperty(
            @Valid @RequestBody PropertyCreateRequestDTO request) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            return new ResponseEntity<>(ApiResponse.success(propertyService.createProperty(request, currentUser)), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-001", e.getMessage(), null)), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyCardDTO>>> getAllProperties() {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAdminCards()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyAdminDetailDTO>> getPropertyById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.getAdminDetail(id)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-003", "Property not found", null)), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyAdminDetailDTO>> updateProperty(
            @PathVariable String id,
            @Valid @RequestBody PropertyUpdateRequestDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.updateProperty(id, request)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-004", e.getMessage(), null)), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable String id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("PROP-005", "Property not found", null)), HttpStatus.NOT_FOUND);
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
                propertyService.searchCards(name, location, developer, listingType, minPrice, maxPrice, true)));
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

    @GetMapping("/amenities")
    public ResponseEntity<ApiResponse<List<AmenityDTO>>> getAmenities(
            @RequestParam(defaultValue = "false") boolean defaultsOnly) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getAmenities(defaultsOnly)));
    }

    @PostMapping("/amenities")
    public ResponseEntity<ApiResponse<AmenityDTO>> createAmenity(@RequestBody Map<String, Object> request) {
        String name = request.get("name") == null ? null : String.valueOf(request.get("name"));
        Boolean defaultAmenity = request.get("defaultAmenity") instanceof Boolean b ? b : false;
        try {
            return new ResponseEntity<>(ApiResponse.success(propertyService.createAmenity(name, defaultAmenity)), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("AMENITY-001", e.getMessage(), null)), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/photos/upload")
    public ResponseEntity<ApiResponse<PropertyPhotoDTO>> uploadPropertyPhoto(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(defaultValue = "0") Integer displayOrder) {
        try {
            PropertyPhotoStorageService.UploadedPropertyPhoto uploadedPhoto =
                    propertyPhotoStorageService.uploadPhoto(photo);
            return new ResponseEntity<>(
                    ApiResponse.success(new PropertyPhotoDTO(uploadedPhoto.getUrl(), displayOrder)),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    ApiResponse.error(new ErrorDetail("PROPERTY-PHOTO-001", e.getMessage(), null)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{propertyId}/units")
    public ResponseEntity<ApiResponse<List<PropertyUnitDTO>>> getPropertyUnits(@PathVariable String propertyId) {
        return ResponseEntity.ok(ApiResponse.success(propertyService.getPropertyUnits(propertyId)));
    }

    @PostMapping("/{propertyId}/units")
    public ResponseEntity<ApiResponse<PropertyUnitDTO>> createPropertyUnit(
            @PathVariable String propertyId,
            @Valid @RequestBody PropertyUnitRequestDTO request) {
        try {
            return new ResponseEntity<>(ApiResponse.success(propertyService.createPropertyUnit(propertyId, request)), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("UNIT-002", e.getMessage(), null)), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{propertyId}/units/{unitId}")
    public ResponseEntity<ApiResponse<PropertyUnitDTO>> updatePropertyUnit(
            @PathVariable String propertyId,
            @PathVariable String unitId,
            @Valid @RequestBody PropertyUnitRequestDTO request) {
        try {
            return ResponseEntity.ok(ApiResponse.success(propertyService.updatePropertyUnit(unitId, request)));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("UNIT-002", e.getMessage(), null)), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{propertyId}/units/{unitId}")
    public ResponseEntity<ApiResponse<Void>> deletePropertyUnit(
            @PathVariable String propertyId,
            @PathVariable String unitId) {
        try {
            propertyService.deletePropertyUnit(unitId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(ApiResponse.error(new ErrorDetail("UNIT-003", "Unit not found", null)), HttpStatus.NOT_FOUND);
        }
    }
}
