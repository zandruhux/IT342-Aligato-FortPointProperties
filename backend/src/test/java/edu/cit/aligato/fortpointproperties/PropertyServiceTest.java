package edu.cit.aligato.fortpointproperties;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyBasicDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitCreateRequest;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyUnitRepository;
import edu.cit.aligato.fortpointproperties.properties.service.PropertyService;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyUnitRepository propertyUnitRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void getAllProperties_mapsToDTOs() {
        Property p = property("p1", "Test Property", "Manila", "ABC Developer", 1000000.0, 5000000.0);
        User creator = new User();
        creator.setFirstname("Agent");
        p.setCreatedBy(creator);
        PropertyUnit unit = unit("u1", "Studio", 25000.0, 36, 15000.0, 2200000.0);
        p.setUnits(List.of(unit));

        when(propertyRepository.findAll()).thenReturn(Collections.singletonList(p));

        List<PropertyDTO> results = propertyService.getAllProperties();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Property", results.get(0).getName());
        assertEquals("Agent", results.get(0).getCreatedBy());
        assertEquals(1, results.get(0).getUnits().size());
        assertEquals("Studio", results.get(0).getUnits().get(0).getUnitType());
    }

    @Test
    void getPropertyById_found() {
        Property p = property("p2", "Other Property", "Cebu", "ABC Developer", 1000000.0, 2000000.0);

        when(propertyRepository.findById("p2")).thenReturn(Optional.of(p));

        PropertyDTO dto = propertyService.getPropertyById("p2");

        assertNotNull(dto);
        assertEquals("Other Property", dto.getName());
        assertEquals("System", dto.getCreatedBy());
    }

    @Test
    void getPropertyById_notFound_throws() {
        when(propertyRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> propertyService.getPropertyById("missing"));
    }

    @Test
    void createProperty_successWithUnits_savesPropertyAndUnits() {
        User currentUser = new User();
        currentUser.setId("admin-1");
        PropertyCreateRequest request = propertyRequest("New Tower", "Cebu City", List.of("Pre-Selling", "RFO"));
        PropertyUnitCreateRequest unitRequest = unitRequest("1-Bedroom");
        request.setUnits(List.of(unitRequest));
        Property savedProperty = property("p-new", request.getName(), request.getLocation(), request.getDeveloper(),
                request.getPriceRangeMin(), request.getPriceRangeMax());
        savedProperty.setCreatedBy(currentUser);

        when(propertyRepository.existsByName("New Tower")).thenReturn(false);
        when(propertyRepository.save(any(Property.class))).thenReturn(savedProperty);
        when(propertyUnitRepository.save(any(PropertyUnit.class))).thenAnswer(invocation -> {
            PropertyUnit unit = invocation.getArgument(0);
            unit.setId("u-new");
            return unit;
        });

        Property result = propertyService.createProperty(request, currentUser);

        assertEquals("p-new", result.getId());
        assertEquals("New Tower", result.getName());
        assertEquals(1, result.getUnits().size());
        assertEquals("1-Bedroom", result.getUnits().get(0).getUnitType());
        assertEquals(currentUser, result.getCreatedBy());

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository, times(2)).save(propertyCaptor.capture());
        Property firstSavedProperty = propertyCaptor.getAllValues().get(0);
        assertEquals("Pre-Selling,RFO", firstSavedProperty.getListingType());
        assertFalse(firstSavedProperty.getPetFriendly());
        assertTrue(firstSavedProperty.getParkingAvailable());
        verify(propertyUnitRepository).save(any(PropertyUnit.class));
    }

    @Test
    void createProperty_duplicateName_throwsAndDoesNotSave() {
        PropertyCreateRequest request = propertyRequest("Existing Tower", "Cebu City", List.of("RFO"));

        when(propertyRepository.existsByName("Existing Tower")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> propertyService.createProperty(request, new User()));

        assertEquals("Property with this name already exists", exception.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
        verify(propertyUnitRepository, never()).save(any(PropertyUnit.class));
    }

    @Test
    void updateProperty_success_replacesEditableFieldsAndUnits() {
        Property existing = property("p-update", "Old Name", "Mandaue", "Old Dev", 1000000.0, 2000000.0);
        existing.setUnits(new java.util.ArrayList<>(List.of(unit("old-unit", "Studio", 10000.0, 12, 8000.0, 1000000.0))));
        PropertyCreateRequest request = propertyRequest("Updated Name", "Lapu-Lapu", List.of("Rent-To-Own"));
        request.setUnits(List.of(unitRequest("Penthouse")));

        when(propertyRepository.findById("p-update")).thenReturn(Optional.of(existing));
        when(propertyRepository.save(existing)).thenReturn(existing);

        Property result = propertyService.updateProperty("p-update", request);

        assertEquals("Updated Name", result.getName());
        assertEquals("Lapu-Lapu", result.getLocation());
        assertEquals("Rent-To-Own", result.getListingType());
        assertEquals(1, result.getUnits().size());
        assertEquals("Penthouse", result.getUnits().get(0).getUnitType());
        assertEquals(existing, result.getUnits().get(0).getProperty());
    }

    @Test
    void updateProperty_missing_throws() {
        when(propertyRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> propertyService.updateProperty("missing", propertyRequest("Name", "Cebu", List.of("RFO"))));
    }

    @Test
    void deleteProperty_success_deletesFavoritesBeforeProperty() {
        when(propertyRepository.existsById("p-delete")).thenReturn(true);

        propertyService.deleteProperty("p-delete");

        verify(favoriteRepository).deleteByPropertyId("p-delete");
        verify(propertyRepository).deleteById("p-delete");
    }

    @Test
    void deleteProperty_missing_throwsAndDoesNotDelete() {
        when(propertyRepository.existsById("missing")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> propertyService.deleteProperty("missing"));

        verify(favoriteRepository, never()).deleteByPropertyId(any(String.class));
        verify(propertyRepository, never()).deleteById(any(String.class));
    }

    @Test
    void searchMethods_delegateAndMapResults() {
        Property p = property("p-search", "Cebu Tower", "Cebu City", "DevCo", 1500000.0, 2500000.0);

        when(propertyRepository.findByNameContainingIgnoreCase("tower")).thenReturn(List.of(p));
        when(propertyRepository.findByLocationContainingIgnoreCase("cebu")).thenReturn(List.of(p));
        when(propertyRepository.findByListingType("RFO")).thenReturn(List.of(p));
        when(propertyRepository.findByPriceRange(1000000.0, 3000000.0)).thenReturn(List.of(p));
        when(propertyRepository.findByPetFriendlyTrue()).thenReturn(List.of(p));
        when(propertyRepository.findByParkingAvailableTrue()).thenReturn(List.of(p));

        assertEquals("Cebu Tower", propertyService.searchByName("tower").get(0).getName());
        assertEquals("Cebu Tower", propertyService.searchByLocation("cebu").get(0).getName());
        assertEquals("Cebu Tower", propertyService.searchByListingType("RFO").get(0).getName());
        assertEquals("Cebu Tower", propertyService.searchByPriceRange(1000000.0, 3000000.0).get(0).getName());
        assertEquals("Cebu Tower", propertyService.getPetFriendlyProperties().get(0).getName());
        assertEquals("Cebu Tower", propertyService.getPropertiesWithParking().get(0).getName());
    }

    @Test
    void searchWithFilters_filtersByNameLocationDeveloperAndPrice() {
        Property match = property("p1", "Cebu Tower", "Cebu City", "DevCo", 1500000.0, 2500000.0);
        Property wrongDeveloper = property("p2", "Cebu Tower Annex", "Cebu City", "OtherDev", 1500000.0, 2500000.0);
        Property wrongPrice = property("p3", "Cebu Tower Budget", "Cebu City", "DevCo", 500000.0, 900000.0);

        when(propertyRepository.findAll()).thenReturn(List.of(match, wrongDeveloper, wrongPrice));

        List<PropertyDTO> results = propertyService.searchWithFilters("tower", "cebu", "devco", 1000000.0, 3000000.0);

        assertEquals(1, results.size());
        assertEquals("p1", results.get(0).getId());
    }

    @Test
    void convertPropertyToBasicDTO_returnsCardFieldsOnly() {
        Property property = property("p-basic", "Basic Tower", "Cebu", "Hidden Dev", 1000000.0, 2000000.0);

        PropertyBasicDTO dto = propertyService.convertPropertyToBasicDTO(property);

        assertEquals("p-basic", dto.getId());
        assertEquals("Basic Tower", dto.getName());
        assertEquals("Cebu", dto.getLocation());
        assertEquals(1000000.0, dto.getPriceRangeMin());
        assertEquals(2000000.0, dto.getPriceRangeMax());
    }

    @Test
    void createPropertyUnit_success() {
        Property property = property("p-units", "Tower", "Cebu", "DevCo", 1000000.0, 2000000.0);
        PropertyUnitCreateRequest request = unitRequest("Loft");

        when(propertyRepository.findById("p-units")).thenReturn(Optional.of(property));
        when(propertyUnitRepository.save(any(PropertyUnit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PropertyUnit result = propertyService.createPropertyUnit("p-units", request);

        assertEquals(property, result.getProperty());
        assertEquals("Loft", result.getUnitType());
        assertEquals(List.of("Bank Financing", "Cash"), result.getFinancingTypes());
    }

    @Test
    void createPropertyUnit_missingProperty_throws() {
        when(propertyRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> propertyService.createPropertyUnit("missing", unitRequest("Loft")));
    }

    @Test
    void getPropertyUnits_mapsUnitsToDTOs() {
        PropertyUnit unit = unit("u-list", "Studio", 25000.0, 36, 15000.0, 2200000.0);

        when(propertyUnitRepository.findByProperty_Id("p-list")).thenReturn(List.of(unit));

        List<PropertyUnitDTO> results = propertyService.getPropertyUnits("p-list");

        assertEquals(1, results.size());
        assertEquals("u-list", results.get(0).getId());
        assertEquals("Studio", results.get(0).getUnitType());
        assertEquals(List.of("Bank Financing", "Cash"), results.get(0).getFinancingTypes());
    }

    @Test
    void updatePropertyUnit_success() {
        PropertyUnit existing = unit("u-update", "Studio", 25000.0, 36, 15000.0, 2200000.0);
        PropertyUnitCreateRequest request = unitRequest("2-Bedroom");

        when(propertyUnitRepository.findById("u-update")).thenReturn(Optional.of(existing));
        when(propertyUnitRepository.save(existing)).thenReturn(existing);

        PropertyUnit result = propertyService.updatePropertyUnit("u-update", request);

        assertEquals("2-Bedroom", result.getUnitType());
        assertEquals(50000.0, result.getReservationFee());
        assertEquals(24, result.getEquityPeriodMonths());
    }

    @Test
    void updatePropertyUnit_missing_throws() {
        when(propertyUnitRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> propertyService.updatePropertyUnit("missing", unitRequest("2-Bedroom")));
    }

    @Test
    void deletePropertyUnit_success() {
        when(propertyUnitRepository.existsById("u-delete")).thenReturn(true);

        propertyService.deletePropertyUnit("u-delete");

        verify(propertyUnitRepository).deleteById("u-delete");
    }

    @Test
    void deletePropertyUnit_missing_throwsAndDoesNotDelete() {
        when(propertyUnitRepository.existsById("missing")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> propertyService.deletePropertyUnit("missing"));

        verify(propertyUnitRepository, never()).deleteById(any(String.class));
    }

    private Property property(String id, String name, String location, String developer, Double minPrice, Double maxPrice) {
        Property property = new Property();
        property.setId(id);
        property.setName(name);
        property.setBasicDescription("Desc");
        property.setDeveloper(developer);
        property.setLocation(location);
        property.setPriceRangeMin(minPrice);
        property.setPriceRangeMax(maxPrice);
        property.setListingType("RFO");
        property.setPetFriendly(true);
        property.setParkingAvailable(true);
        property.setTurnoverDate("2026-12");
        property.setAmenities("Pool, Gym");
        property.setKeySellingPoints("Near transit");
        property.setBrochurePdfUrl("https://example.com/brochure.pdf");
        property.setInventoryLink("https://example.com/inventory");
        return property;
    }

    private PropertyCreateRequest propertyRequest(String name, String location, List<String> listingTypes) {
        PropertyCreateRequest request = new PropertyCreateRequest();
        request.setName(name);
        request.setBasicDescription("Desc");
        request.setDeveloper("DevCo");
        request.setPriceRangeMin(1000000.0);
        request.setPriceRangeMax(5000000.0);
        request.setLocation(location);
        request.setListingType(listingTypes);
        request.setPetFriendly(null);
        request.setParkingAvailable(true);
        request.setTurnoverDate("2026-12");
        request.setAmenities("Pool, Gym");
        request.setKeySellingPoints("Near transit");
        request.setBrochurePdfUrl("https://example.com/brochure.pdf");
        request.setInventoryLink("https://example.com/inventory");
        return request;
    }

    private PropertyUnit unit(String id, String unitType, Double reservationFee, Integer equityMonths,
                              Double monthlyEquity, Double totalSellingPrice) {
        PropertyUnit unit = new PropertyUnit();
        unit.setId(id);
        unit.setUnitType(unitType);
        unit.setFloorArea(42.0);
        unit.setLotArea(60.0);
        unit.setReservationFee(reservationFee);
        unit.setEquityPeriodMonths(equityMonths);
        unit.setMonthlyEquity(monthlyEquity);
        unit.setTotalSellingPrice(totalSellingPrice);
        unit.setFinancingTypes(List.of("Bank Financing", "Cash"));
        return unit;
    }

    private PropertyUnitCreateRequest unitRequest(String unitType) {
        PropertyUnitCreateRequest request = new PropertyUnitCreateRequest();
        request.setUnitType(unitType);
        request.setFloorArea(55.0);
        request.setLotArea(70.0);
        request.setReservationFee(50000.0);
        request.setEquityPeriodMonths(24);
        request.setMonthlyEquity(30000.0);
        request.setTotalSellingPrice(3500000.0);
        request.setFinancingTypes(List.of("Bank Financing", "Cash"));
        return request;
    }
}
