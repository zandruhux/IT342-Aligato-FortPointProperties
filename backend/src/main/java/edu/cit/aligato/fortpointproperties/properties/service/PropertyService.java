package edu.cit.aligato.fortpointproperties.properties.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.properties.dto.AmenityDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyAdminDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyAgentDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyPhotoDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUpdateRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUserDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Amenity;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyPhoto;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.repository.AmenityRepository;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository.PropertyCardRow;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyUnitRepository;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyUnitRepository propertyUnitRepository;
    private final AmenityRepository amenityRepository;
    private final FavoriteRepository favoriteRepository;

    public PropertyService(
            PropertyRepository propertyRepository,
            PropertyUnitRepository propertyUnitRepository,
            AmenityRepository amenityRepository,
            FavoriteRepository favoriteRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyUnitRepository = propertyUnitRepository;
        this.amenityRepository = amenityRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public PropertyAdminDetailDTO createProperty(PropertyCreateRequestDTO request, User currentUser) {
        if (propertyRepository.existsByName(request.name)) {
            throw new IllegalArgumentException("Property with this name already exists");
        }

        Property property = new Property();
        applyRequest(property, request, true);
        property.setCreatedBy(currentUser);
        return toAdminDetail(propertyRepository.save(property));
    }

    @Transactional
    public PropertyAdminDetailDTO updateProperty(String id, PropertyUpdateRequestDTO request) {
        Property property = getPropertyOrThrow(id);
        if (!property.getName().equalsIgnoreCase(request.name)
                && propertyRepository.existsByNameIgnoreCaseAndIdNot(request.name, id)) {
            throw new IllegalArgumentException("Property with this name already exists");
        }

        applyRequest(property, request, request.units != null);
        return toAdminDetail(propertyRepository.save(property));
    }

    @Transactional
    public void deleteProperty(String id) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("Property not found");
        }
        favoriteRepository.deleteByPropertyId(id);
        propertyRepository.deleteById(id);
    }

    public List<PropertyCardDTO> getPublicCards() {
        return propertyRepository.findCardRows(false).stream()
                .map(this::toCard)
                .toList();
    }

    public List<PropertyCardDTO> getAdminCards() {
        return propertyRepository.findCardRows(true).stream()
                .map(this::toCard)
                .toList();
    }

    public PropertyCardDTO getPublicCardById(String id) {
        Property property = getPropertyOrThrow(id);
        ensureVisible(property);
        return toCard(property);
    }

    public PropertyUserDetailDTO getUserDetail(String id) {
        Property property = getPropertyOrThrow(id);
        ensureVisible(property);
        return toUserDetail(property);
    }

    public PropertyAgentDetailDTO getAgentDetail(String id) {
        Property property = getPropertyOrThrow(id);
        ensureVisible(property);
        return toAgentDetail(property);
    }

    public PropertyAdminDetailDTO getAdminDetail(String id) {
        return toAdminDetail(getPropertyOrThrow(id));
    }

    public List<PropertyCardDTO> searchCards(String name, String location, String developer,
                                             Double minPrice, Double maxPrice, boolean includeHidden) {
        return searchCards(name, location, developer, null, minPrice, maxPrice, includeHidden);
    }

    public List<PropertyCardDTO> searchCards(String name, String location, String developer, ListingType listingType,
                                             Double minPrice, Double maxPrice, boolean includeHidden) {
        return propertyRepository.searchCardRows(
                        blankToNull(name),
                        blankToNull(location),
                        blankToNull(developer),
                        listingType != null ? listingType.name() : null,
                        minPrice,
                        maxPrice,
                        includeHidden)
                .stream()
                .map(this::toCard)
                .toList();
    }

    public List<AmenityDTO> getAmenities(boolean defaultsOnly) {
        return (defaultsOnly
                ? amenityRepository.findDefaultAmenitiesWithUsageCounts()
                : amenityRepository.findAllWithUsageCounts())
                .stream()
                .map(this::toAmenityDTO)
                .toList();
    }

    @Transactional
    public AmenityDTO createAmenity(String name, Boolean defaultAmenity) {
        Amenity amenity = findOrCreateAmenity(name, defaultAmenity);
        return toAmenityDTO(amenity);
    }

    @Transactional
    public PropertyUnitDTO createPropertyUnit(String propertyId, PropertyUnitRequestDTO request) {
        Property property = getPropertyOrThrow(propertyId);
        PropertyUnit unit = toUnitEntity(request, property);
        return toUnitDTO(propertyUnitRepository.save(unit));
    }

    public List<PropertyUnitDTO> getPropertyUnits(String propertyId) {
        return propertyUnitRepository.findByProperty_Id(propertyId).stream()
                .map(this::toUnitDTO)
                .toList();
    }

    @Transactional
    public PropertyUnitDTO updatePropertyUnit(String unitId, PropertyUnitRequestDTO request) {
        PropertyUnit unit = propertyUnitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
        applyUnitRequest(unit, request);
        return toUnitDTO(propertyUnitRepository.save(unit));
    }

    @Transactional
    public void deletePropertyUnit(String unitId) {
        if (!propertyUnitRepository.existsById(unitId)) {
            throw new IllegalArgumentException("Unit not found");
        }
        propertyUnitRepository.deleteById(unitId);
    }

    private void applyRequest(Property property, PropertyCreateRequestDTO request, boolean replaceUnits) {
        property.setName(request.name);
        property.setBasicDescription(request.basicDescription);
        property.setDeveloper(request.developer);
        property.setLocation(request.location);
        property.setListingTypes(new HashSet<>(safeList(request.listingTypes)));
        property.setFinancingTypes(new HashSet<>(safeList(request.financingTypes)));
        property.setPetFriendly(Objects.requireNonNullElse(request.petFriendly, false));
        property.setParkingAvailable(Objects.requireNonNullElse(request.parkingAvailable, false));
        property.setHasPromo(Objects.requireNonNullElse(request.hasPromo, false));
        property.setFeatured(Objects.requireNonNullElse(request.featured, false));
        property.setVisible(Objects.requireNonNullElse(request.visible, true));
        property.setTurnoverDate(request.turnoverDate);
        property.setKeySellingPoints(request.keySellingPoints);
        property.setBrochurePdfUrl(request.brochurePdfUrl);
        property.setInventoryLink(request.inventoryLink);
        property.setAmenities(resolveAmenities(request.amenityIds, request.customAmenities));

        if (replaceUnits) {
            property.getUnits().clear();
            for (PropertyUnitRequestDTO unitRequest : safeList(request.units)) {
                property.getUnits().add(toUnitEntity(unitRequest, property));
            }
        }

        property.getPhotos().clear();
        int fallbackOrder = 0;
        for (PropertyPhotoDTO photoRequest : safeList(request.photos)) {
            if (photoRequest.photoUrl == null || photoRequest.photoUrl.isBlank()) continue;
            property.getPhotos().add(new PropertyPhoto(
                    property,
                    photoRequest.photoUrl.trim(),
                    photoRequest.displayOrder != null ? photoRequest.displayOrder : fallbackOrder));
            fallbackOrder++;
        }
    }

    private Set<Amenity> resolveAmenities(List<String> amenityIds, List<String> customAmenities) {
        Set<Amenity> amenities = new HashSet<>();
        for (String amenityId : safeList(amenityIds)) {
            if (amenityId == null || amenityId.isBlank()) continue;
            amenities.add(amenityRepository.findById(amenityId)
                    .orElseThrow(() -> new IllegalArgumentException("Amenity not found: " + amenityId)));
        }
        for (String customAmenity : safeList(customAmenities)) {
            if (customAmenity == null || customAmenity.isBlank()) continue;
            amenities.add(findOrCreateAmenity(customAmenity, false));
        }
        return amenities;
    }

    private Amenity findOrCreateAmenity(String name, Boolean defaultAmenity) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Amenity name is required");
        }
        return amenityRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> amenityRepository.save(new Amenity(trimmed, defaultAmenity)));
    }

    private PropertyUnit toUnitEntity(PropertyUnitRequestDTO request, Property property) {
        PropertyUnit unit = new PropertyUnit();
        unit.setProperty(property);
        applyUnitRequest(unit, request);
        return unit;
    }

    private void applyUnitRequest(PropertyUnit unit, PropertyUnitRequestDTO request) {
        unit.setUnitType(request.unitType);
        unit.setFloorArea(request.floorArea);
        unit.setLotArea(request.lotArea);
        unit.setReservationFee(request.reservationFee);
        unit.setEquityPeriodMonths(request.equityPeriodMonths);
        unit.setMonthlyEquity(request.monthlyEquity);
        unit.setTotalSellingPrice(request.totalSellingPrice);
    }

    private PropertyCardDTO toCard(Property property) {
        PropertyCardDTO dto = new PropertyCardDTO();
        dto.id = property.getId();
        dto.name = property.getName();
        dto.basicDescription = property.getBasicDescription();
        dto.location = property.getLocation();
        dto.priceRangeMin = calculateMinPrice(property);
        dto.priceRangeMax = calculateMaxPrice(property);
        dto.listingTypes = sortedListingTypes(property);
        dto.hasPromo = property.getHasPromo();
        dto.coverPhotoUrl = property.getPhotos().stream()
                .sorted(Comparator.comparing(PropertyPhoto::getDisplayOrder))
                .map(PropertyPhoto::getPhotoUrl)
                .findFirst()
                .orElse(null);
        return dto;
    }

    private PropertyCardDTO toCard(PropertyCardRow row) {
        PropertyCardDTO dto = new PropertyCardDTO();
        dto.id = row.getId();
        dto.name = row.getName();
        dto.basicDescription = row.getBasicDescription();
        dto.location = row.getLocation();
        dto.priceRangeMin = row.getPriceRangeMin();
        dto.priceRangeMax = row.getPriceRangeMax();
        dto.listingTypes = parseListingTypes(row.getListingTypes());
        dto.hasPromo = row.getHasPromo();
        dto.coverPhotoUrl = row.getCoverPhotoUrl();
        return dto;
    }

    private List<ListingType> parseListingTypes(String listingTypes) {
        if (listingTypes == null || listingTypes.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(listingTypes.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(ListingType::valueOf)
                .sorted()
                .toList();
    }

    private PropertyUserDetailDTO toUserDetail(Property property) {
        PropertyUserDetailDTO dto = new PropertyUserDetailDTO();
        fillUserFields(dto, property);
        return dto;
    }

    private PropertyAgentDetailDTO toAgentDetail(Property property) {
        PropertyAgentDetailDTO dto = new PropertyAgentDetailDTO();
        fillUserFields(dto, property);
        dto.developer = property.getDeveloper();
        dto.keySellingPoints = property.getKeySellingPoints();
        dto.brochurePdfUrl = property.getBrochurePdfUrl();
        dto.inventoryLink = property.getInventoryLink();
        return dto;
    }

    private PropertyAdminDetailDTO toAdminDetail(Property property) {
        PropertyAdminDetailDTO dto = new PropertyAdminDetailDTO();
        fillUserFields(dto, property);
        dto.developer = property.getDeveloper();
        dto.keySellingPoints = property.getKeySellingPoints();
        dto.brochurePdfUrl = property.getBrochurePdfUrl();
        dto.inventoryLink = property.getInventoryLink();
        dto.featured = property.getFeatured();
        dto.visible = property.getVisible();
        dto.createdAt = property.getCreatedAt();
        dto.updatedAt = property.getUpdatedAt();
        dto.createdBy = property.getCreatedBy() != null && property.getCreatedBy().getFirstname() != null
                ? property.getCreatedBy().getFirstname() + " " + property.getCreatedBy().getLastname()
                : "System";
        return dto;
    }

    private void fillUserFields(PropertyUserDetailDTO dto, Property property) {
        dto.id = property.getId();
        dto.name = property.getName();
        dto.basicDescription = property.getBasicDescription();
        dto.location = property.getLocation();
        dto.priceRangeMin = calculateMinPrice(property);
        dto.priceRangeMax = calculateMaxPrice(property);
        dto.listingTypes = sortedListingTypes(property);
        dto.financingTypes = property.getFinancingTypes().stream().sorted().toList();
        dto.petFriendly = property.getPetFriendly();
        dto.parkingAvailable = property.getParkingAvailable();
        dto.hasPromo = property.getHasPromo();
        dto.turnoverDate = property.getTurnoverDate();
        dto.amenities = property.getAmenities().stream()
                .sorted(Comparator.comparing(Amenity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toAmenityDTO)
                .toList();
        dto.photos = property.getPhotos().stream()
                .sorted(Comparator.comparing(PropertyPhoto::getDisplayOrder))
                .map(photo -> new PropertyPhotoDTO(photo.getPhotoUrl(), photo.getDisplayOrder()))
                .toList();
        dto.units = property.getUnits().stream()
                .sorted(Comparator.comparing(PropertyUnit::getUnitType, String.CASE_INSENSITIVE_ORDER))
                .map(this::toUnitDTO)
                .toList();
    }

    private PropertyUnitDTO toUnitDTO(PropertyUnit unit) {
        PropertyUnitDTO dto = new PropertyUnitDTO();
        dto.id = unit.getId();
        dto.unitType = unit.getUnitType();
        dto.floorArea = unit.getFloorArea();
        dto.lotArea = unit.getLotArea();
        dto.reservationFee = unit.getReservationFee();
        dto.equityPeriodMonths = unit.getEquityPeriodMonths();
        dto.monthlyEquity = unit.getMonthlyEquity();
        dto.totalSellingPrice = unit.getTotalSellingPrice();
        dto.createdAt = unit.getCreatedAt();
        dto.updatedAt = unit.getUpdatedAt();
        return dto;
    }

    private AmenityDTO toAmenityDTO(Amenity amenity) {
        return new AmenityDTO(amenity.getId(), amenity.getName(), amenity.getDefaultAmenity());
    }

    private AmenityDTO toAmenityDTO(AmenityRepository.AmenityUsageRow amenity) {
        return new AmenityDTO(
                amenity.getId(),
                amenity.getName(),
                amenity.getDefaultAmenity(),
                amenity.getUsageCount());
    }

    private Double calculateMinPrice(Property property) {
        return property.getUnits().stream()
                .map(PropertyUnit::getTotalSellingPrice)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);
    }

    private Double calculateMaxPrice(Property property) {
        return property.getUnits().stream()
                .map(PropertyUnit::getTotalSellingPrice)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(null);
    }

    private Property getPropertyOrThrow(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
    }

    private void ensureVisible(Property property) {
        if (!Boolean.TRUE.equals(property.getVisible())) {
            throw new IllegalArgumentException("Property not found");
        }
    }

    private List<ListingType> sortedListingTypes(Property property) {
        return property.getListingTypes().stream().sorted().toList();
    }

    private Comparator<Property> featuredFirst() {
        return Comparator.comparing((Property p) -> Boolean.TRUE.equals(p.getFeatured())).reversed()
                .thenComparing(Property::getName, String.CASE_INSENSITIVE_ORDER);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? new ArrayList<>() : values;
    }
}
