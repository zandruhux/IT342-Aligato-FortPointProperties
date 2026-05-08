package edu.cit.aligato.fortpointproperties.properties.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitDTO;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyUnitRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyUnitRepository propertyUnitRepository;
    private final FavoriteRepository favoriteRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            PropertyUnitRepository propertyUnitRepository,
            FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyUnitRepository = propertyUnitRepository;
        this.favoriteRepository = favoriteRepository;
    }

    // --- CRUD Operations ---

    //CREATE NEW PROPERTY (with optional units)
    @Transactional
    public Property createProperty(PropertyCreateRequest request, User currentUser) {
    if (propertyRepository.existsByName(request.getName())) {
        throw new IllegalArgumentException("Property with this name already exists");
    }

    Property property = new Property();
    property.setName(request.getName());
    property.setBasicDescription(request.getBasicDescription());
    property.setDeveloper(request.getDeveloper());
    property.setPriceRangeMin(request.getPriceRangeMin());
    property.setPriceRangeMax(request.getPriceRangeMax());
    property.setLocation(request.getLocation());
    
    // Convert listingType array to comma-separated string
    if (request.getListingType() != null && !request.getListingType().isEmpty()) {
        String listingTypeStr = String.join(",", request.getListingType());
        property.setListingType(listingTypeStr);
    }
    
    property.setPetFriendly(Objects.requireNonNullElse(request.getPetFriendly(), false));
    property.setParkingAvailable(Objects.requireNonNullElse(request.getParkingAvailable(), false));
    property.setTurnoverDate(request.getTurnoverDate());
    property.setAmenities(request.getAmenities());
    property.setKeySellingPoints(request.getKeySellingPoints());
    property.setBrochurePdfUrl(request.getBrochurePdfUrl());
    property.setInventoryLink(request.getInventoryLink());
    property.setCreatedBy(currentUser);

    // Initialize units list (empty by default, can be populated below)
    property.setUnits(new ArrayList<>());

    // Save property first to generate ID
    Property savedProperty = propertyRepository.save(property);

    // Handle units if provided - save them explicitly via repository
    if (request.getUnits() != null && !request.getUnits().isEmpty()) {
        List<PropertyUnit> savedUnits = new ArrayList<>();
        for (PropertyUnitCreateRequest unitRequest : request.getUnits()) {
            PropertyUnit unit = new PropertyUnit();
            unit.setProperty(savedProperty);
            unit.setUnitType(unitRequest.getUnitType());
            unit.setFloorArea(unitRequest.getFloorArea());
            unit.setLotArea(unitRequest.getLotArea());
            unit.setReservationFee(unitRequest.getReservationFee());
            unit.setEquityPeriodMonths(unitRequest.getEquityPeriodMonths());
            unit.setMonthlyEquity(unitRequest.getMonthlyEquity());
            unit.setTotalSellingPrice(unitRequest.getTotalSellingPrice());
            unit.setFinancingTypes(unitRequest.getFinancingTypes());
            
            // Explicitly save the unit via repository (handles @ElementCollection properly)
            PropertyUnit savedUnit = propertyUnitRepository.save(unit);
            savedUnits.add(savedUnit);
        }
        
        // Update property with saved units and save again
        savedProperty.setUnits(savedUnits);
        savedProperty = propertyRepository.save(savedProperty);
    }

    return savedProperty;
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
    @Transactional
    public Property updateProperty(String id, PropertyCreateRequest request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setName(request.getName());
        property.setBasicDescription(request.getBasicDescription());
        property.setDeveloper(request.getDeveloper());
        property.setPriceRangeMin(request.getPriceRangeMin());
        property.setPriceRangeMax(request.getPriceRangeMax());
        property.setLocation(request.getLocation());
        
        // Convert listingType array to comma-separated string
        if (request.getListingType() != null && !request.getListingType().isEmpty()) {
            String listingTypeStr = String.join(",", request.getListingType());
            property.setListingType(listingTypeStr);
        }
        
        property.setPetFriendly(request.getPetFriendly());
        property.setParkingAvailable(request.getParkingAvailable());
        property.setTurnoverDate(request.getTurnoverDate());
        property.setAmenities(request.getAmenities());
        property.setKeySellingPoints(request.getKeySellingPoints());
        property.setBrochurePdfUrl(request.getBrochurePdfUrl());
        property.setInventoryLink(request.getInventoryLink());

        // Handle units cascade (similar to createProperty)
        if (request.getUnits() != null && !request.getUnits().isEmpty()) {
            // Clear existing units and add new ones
            property.getUnits().clear();
            
            for (PropertyUnitCreateRequest unitRequest : request.getUnits()) {
                PropertyUnit unit = new PropertyUnit();
                unit.setProperty(property);
                unit.setUnitType(unitRequest.getUnitType());
                unit.setFloorArea(unitRequest.getFloorArea());
                unit.setLotArea(unitRequest.getLotArea());
                unit.setReservationFee(unitRequest.getReservationFee());
                unit.setEquityPeriodMonths(unitRequest.getEquityPeriodMonths());
                unit.setMonthlyEquity(unitRequest.getMonthlyEquity());
                unit.setTotalSellingPrice(unitRequest.getTotalSellingPrice());
                unit.setFinancingTypes(unitRequest.getFinancingTypes());
                
                property.getUnits().add(unit);
            }
        }

        return propertyRepository.save(property);
    }

    /**
     * Delete a property (Admin only)
     */
    @Transactional
    public void deleteProperty(String id) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("Property not found");
        }
        favoriteRepository.deleteByPropertyId(id);
        propertyRepository.deleteById(id);
    }

    // --- Search Operations ---

    /**
     * Search properties by name
     */
    public List<PropertyDTO> searchByName(String name) {
        return propertyRepository.findByNameContainingIgnoreCase(name).stream()
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
     * Search properties by price range
     */
    public List<PropertyDTO> searchByPriceRange(Double minPrice, Double maxPrice) {
        return propertyRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search properties using multiple optional filters. All parameters are optional;
     * if a parameter is null it will not be applied. This performs the filtering
     * on the server side for scalability and to keep client logic simple.
     */
    public List<PropertyDTO> searchWithFilters(String name, String location, String developer,
                                               Double minPrice, Double maxPrice) {
        return getAllProperties().stream()
                .filter(p -> name == null || (p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(p -> location == null || (p.getLocation() != null && p.getLocation().toLowerCase().contains(location.toLowerCase())))
                .filter(p -> developer == null || (p.getDeveloper() != null && p.getDeveloper().toLowerCase().contains(developer.toLowerCase())))
                .filter(p -> minPrice == null || (p.getPriceRangeMin() != null && p.getPriceRangeMin() <= (p.getPriceRangeMax() != null ? p.getPriceRangeMax() : Double.MAX_VALUE) && p.getPriceRangeMax() >= minPrice))
                .filter(p -> maxPrice == null || (p.getPriceRangeMax() != null && p.getPriceRangeMin() <= maxPrice && p.getPriceRangeMax() >= (p.getPriceRangeMin() != null ? p.getPriceRangeMin() : 0)))
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
     * Includes nested units as PropertyUnitDTO list
     */
    private PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setName(property.getName());
        dto.setBasicDescription(property.getBasicDescription());
        dto.setDeveloper(property.getDeveloper());
        dto.setPriceRangeMin(property.getPriceRangeMin());
        dto.setPriceRangeMax(property.getPriceRangeMax());
        dto.setLocation(property.getLocation());
        dto.setListingType(property.getListingType());
        dto.setPetFriendly(property.getPetFriendly());
        dto.setParkingAvailable(property.getParkingAvailable());
        dto.setTurnoverDate(property.getTurnoverDate());
        dto.setAmenities(property.getAmenities());
        dto.setKeySellingPoints(property.getKeySellingPoints());
        dto.setBrochurePdfUrl(property.getBrochurePdfUrl());
        dto.setInventoryLink(property.getInventoryLink());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        
        // Map createdBy user firstname only if not null
        if (property.getCreatedBy() != null && property.getCreatedBy().getFirstname() != null) {
            dto.setCreatedBy(property.getCreatedBy().getFirstname());
        } else {
            dto.setCreatedBy("System");
        }

        // Convert and set units if available
        if (property.getUnits() != null && !property.getUnits().isEmpty()) {
            List<PropertyUnitDTO> unitDTOs = property.getUnits().stream()
                    .map(this::convertUnitToDTO)
                    .collect(Collectors.toList());
            dto.setUnits(unitDTOs);
        }

        
        return dto;
    }

    /**
     * Convert Property entity to basic DTO (for card views)
     */
    public PropertyBasicDTO convertPropertyToBasicDTO(Property property) {
        return new PropertyBasicDTO(
                property.getId(),
                property.getName(),
                property.getBasicDescription(),
                property.getLocation(),
                property.getPriceRangeMin(),
                property.getPriceRangeMax()
        );
    }

    // --- Unit CRUD Operations ---

    /**
     * Create a new unit for a property
     */
    @Transactional
    public PropertyUnit createPropertyUnit(String propertyId, PropertyUnitCreateRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        PropertyUnit unit = new PropertyUnit();
        unit.setProperty(property);
        unit.setUnitType(request.getUnitType());
        unit.setFloorArea(request.getFloorArea());
        unit.setLotArea(request.getLotArea());
        unit.setReservationFee(request.getReservationFee());
        unit.setEquityPeriodMonths(request.getEquityPeriodMonths());
        unit.setMonthlyEquity(request.getMonthlyEquity());
        unit.setTotalSellingPrice(request.getTotalSellingPrice());
        unit.setFinancingTypes(request.getFinancingTypes());

        return propertyUnitRepository.save(unit);
    }

    /**
     * Get all units for a property
     */
    public List<PropertyUnitDTO> getPropertyUnits(String propertyId) {
        List<PropertyUnit> units = propertyUnitRepository.findByProperty_Id(propertyId);
        return units.stream()
                .map(this::convertUnitToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update a unit
     */
    @Transactional
    public PropertyUnit updatePropertyUnit(String unitId, PropertyUnitCreateRequest request) {
        PropertyUnit unit = propertyUnitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Unit not found"));

        unit.setUnitType(request.getUnitType());
        unit.setFloorArea(request.getFloorArea());
        unit.setLotArea(request.getLotArea());
        unit.setReservationFee(request.getReservationFee());
        unit.setEquityPeriodMonths(request.getEquityPeriodMonths());
        unit.setMonthlyEquity(request.getMonthlyEquity());
        unit.setTotalSellingPrice(request.getTotalSellingPrice());
        unit.setFinancingTypes(request.getFinancingTypes());

        return propertyUnitRepository.save(unit);
    }

    /**
     * Delete a unit
     */
    @Transactional
    public void deletePropertyUnit(String unitId) {
        if (!propertyUnitRepository.existsById(unitId)) {
            throw new IllegalArgumentException("Unit not found");
        }
        propertyUnitRepository.deleteById(unitId);
    }

    /**
     * Convert PropertyUnit entity to PropertyUnitDTO
     */
    private PropertyUnitDTO convertUnitToDTO(PropertyUnit unit) {
        PropertyUnitDTO dto = new PropertyUnitDTO();
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
        return dto;
    }
}
