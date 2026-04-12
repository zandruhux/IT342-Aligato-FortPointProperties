package edu.cit.aligato.fortpointproperties.properties.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.cit.aligato.fortpointproperties.entity.User;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // --- CRUD Operations ---

    //CREATE NEW PROPERTY
    public Property createProperty(PropertyCreateRequest request, User currentUser) {
        if (propertyRepository.existsByPropertyName(request.getPropertyName())) {
            throw new IllegalArgumentException("Property with this name already exists");
        }

        Property property = new Property();
        property.setPropertyName(request.getPropertyName());
        property.setDescription(request.getDescription());
        property.setDeveloperName(request.getDeveloperName());
        property.setPriceRangeMin(request.getPriceRangeMin());
        property.setPriceRangeMax(request.getPriceRangeMax());
        property.setLocation(request.getLocation());
        property.setPropertyType(request.getPropertyType());
        property.setUnitType(request.getUnitType());
        property.setListingType(request.getListingType());
        property.setPetFriendly(request.getPetFriendly() != null ? request.getPetFriendly() : false);
        property.setParkingAvailable(request.getParkingAvailable() != null ? request.getParkingAvailable() : false);
        property.setTurnoverDate(request.getTurnoverDate());
        property.setAmenities(request.getAmenities());
        property.setPriceComputations(request.getPriceComputations());
        property.setDeveloperLinks(request.getDeveloperLinks());
        property.setPitchReadyPhrases(request.getPitchReadyPhrases());
        property.setCreatedBy(currentUser);

        return propertyRepository.save(property);
    }

    /**
     * Get all properties (basic list view)
     */
    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get property by ID (basic view)
     */
    public PropertyDTO getPropertyById(String id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        return convertToDTO(property);
    }



    /**
     * Update a property (Admin only)
     */
    public Property updateProperty(String id, PropertyCreateRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setPropertyName(request.getPropertyName());
        property.setDescription(request.getDescription());
        property.setDeveloperName(request.getDeveloperName());
        property.setPriceRangeMin(request.getPriceRangeMin());
        property.setPriceRangeMax(request.getPriceRangeMax());
        property.setLocation(request.getLocation());
        property.setPropertyType(request.getPropertyType());
        property.setUnitType(request.getUnitType());
        property.setListingType(request.getListingType());
        property.setPetFriendly(request.getPetFriendly());
        property.setParkingAvailable(request.getParkingAvailable());
        property.setTurnoverDate(request.getTurnoverDate());
        property.setAmenities(request.getAmenities());
        property.setPriceComputations(request.getPriceComputations());
        property.setDeveloperLinks(request.getDeveloperLinks());
        property.setPitchReadyPhrases(request.getPitchReadyPhrases());

        return propertyRepository.save(property);
    }

    /**
     * Delete a property (Admin only)
     */
    public void deleteProperty(String id) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("Property not found");
        }
        propertyRepository.deleteById(id);
    }

    // --- Search Operations ---

    /**
     * Search properties by name
     */
    public List<PropertyDTO> searchByName(String name) {
        return propertyRepository.findByPropertyNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties by location
     */
    public List<PropertyDTO> searchByLocation(String location) {
        return propertyRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties by listing type
     */
    public List<PropertyDTO> searchByListingType(String listingType) {
        return propertyRepository.findByListingType(listingType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties by property type
     */
    public List<PropertyDTO> searchByPropertyType(String propertyType) {
        return propertyRepository.findByPropertyType(propertyType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search pet-friendly properties
     */
    public List<PropertyDTO> getPetFriendlyProperties() {
        return propertyRepository.findByPetFriendlyTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties with parking
     */
    public List<PropertyDTO> getPropertiesWithParking() {
        return propertyRepository.findByParkingAvailableTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---

    /**
     * Convert Property entity to basic DTO (public method for controllers)
     */
    public PropertyDTO convertPropertyToDTO(Property property) {
        return convertToDTO(property);
    }

    /**
     * Convert Property entity to basic DTO
     */
    private PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setPropertyName(property.getPropertyName());
        dto.setDescription(property.getDescription());
        dto.setDeveloperName(property.getDeveloperName());
        dto.setPriceRangeMin(property.getPriceRangeMin());
        dto.setPriceRangeMax(property.getPriceRangeMax());
        dto.setLocation(property.getLocation());
        dto.setPropertyType(property.getPropertyType());
        dto.setUnitType(property.getUnitType());
        dto.setListingType(property.getListingType());
        dto.setPetFriendly(property.getPetFriendly());
        dto.setParkingAvailable(property.getParkingAvailable());
        dto.setTurnoverDate(property.getTurnoverDate());
        dto.setAmenities(property.getAmenities());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        
        // Map createdBy user firstname only if not null
        if (property.getCreatedBy() != null && property.getCreatedBy().getFirstname() != null) {
            dto.setCreatedBy(property.getCreatedBy().getFirstname());
        } else {
            dto.setCreatedBy("System");
        }
        
        return dto;
    }

    /**
     * Convert Property entity to basic DTO (for card views)
     */
    public PropertyBasicDTO convertPropertyToBasicDTO(Property property) {
        return new PropertyBasicDTO(
                property.getId(),
                property.getPropertyName(),
                property.getDescription(),
                property.getLocation(),
                property.getPriceRangeMin(),
                property.getPriceRangeMax()
        );
    }
}
