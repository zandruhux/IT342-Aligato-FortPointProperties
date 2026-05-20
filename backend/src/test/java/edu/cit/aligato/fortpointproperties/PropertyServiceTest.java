package edu.cit.aligato.fortpointproperties;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyAdminDetailDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCardDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyCreateRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.dto.PropertyUnitRequestDTO;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;
import edu.cit.aligato.fortpointproperties.properties.enums.FinancingType;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;
import edu.cit.aligato.fortpointproperties.properties.repository.AmenityRepository;
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
    private AmenityRepository amenityRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void createProperty_derivesPriceRangeFromUnits() {
        User currentUser = new User();
        currentUser.setId("admin-1");
        PropertyCreateRequestDTO request = request();

        when(propertyRepository.existsByName("New Tower")).thenReturn(false);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property saved = invocation.getArgument(0);
            saved.setId("p-new");
            return saved;
        });

        PropertyAdminDetailDTO result = propertyService.createProperty(request, currentUser);

        assertEquals("New Tower", result.name);
        assertEquals(ListingType.PRE_SELLING, result.listingTypes.get(0));
        assertEquals(FinancingType.BANK_FINANCING, result.financingTypes.get(0));
        assertEquals(2000000.0, result.priceRangeMin);
        assertEquals(3500000.0, result.priceRangeMax);
    }

    @Test
    void getUserDetail_hiddenProperty_throws() {
        Property hidden = property("p-hidden", false);
        when(propertyRepository.findById("p-hidden")).thenReturn(Optional.of(hidden));

        assertThrows(IllegalArgumentException.class, () -> propertyService.getUserDetail("p-hidden"));
    }

    @Test
    void getPublicCards_onlyVisibleAndCardsOnly() {
        when(propertyRepository.findCardRows(false)).thenReturn(List.of(new TestPropertyCardRow()));

        List<PropertyCardDTO> cards = propertyService.getPublicCards();

        assertEquals(1, cards.size());
        assertEquals("Tower", cards.get(0).name);
        assertEquals(2000000.0, cards.get(0).priceRangeMin);
    }

    @Test
    void deleteProperty_deletesFavoritesBeforeProperty() {
        when(propertyRepository.existsById("p-delete")).thenReturn(true);

        propertyService.deleteProperty("p-delete");

        verify(favoriteRepository).deleteByPropertyId("p-delete");
        verify(propertyRepository).deleteById("p-delete");
    }

    private PropertyCreateRequestDTO request() {
        PropertyCreateRequestDTO request = new PropertyCreateRequestDTO();
        request.name = "New Tower";
        request.basicDescription = "Desc";
        request.developer = "DevCo";
        request.location = "Cebu";
        request.listingTypes = List.of(ListingType.PRE_SELLING);
        request.financingTypes = List.of(FinancingType.BANK_FINANCING);
        request.turnoverDate = "2026-12";
        request.visible = true;
        request.units = List.of(unitRequest("Studio", 2000000.0), unitRequest("1BR", 3500000.0));
        return request;
    }

    private PropertyUnitRequestDTO unitRequest(String type, Double price) {
        PropertyUnitRequestDTO unit = new PropertyUnitRequestDTO();
        unit.unitType = type;
        unit.floorArea = 30.0;
        unit.lotArea = null;
        unit.reservationFee = 25000.0;
        unit.equityPeriodMonths = 24;
        unit.monthlyEquity = 15000.0;
        unit.totalSellingPrice = price;
        return unit;
    }

    private Property property(String id, boolean visible) {
        Property property = new Property();
        property.setId(id);
        property.setName("Tower");
        property.setBasicDescription("Desc");
        property.setDeveloper("DevCo");
        property.setLocation("Cebu");
        property.setVisible(visible);
        property.setTurnoverDate("2026-12");
        property.getListingTypes().add(ListingType.RFO);
        property.getFinancingTypes().add(FinancingType.SPOT_CASH);
        property.getUnits().add(unit(property, "Studio", 2000000.0));
        return property;
    }

    private PropertyUnit unit(Property property, String type, Double price) {
        PropertyUnit unit = new PropertyUnit();
        unit.setProperty(property);
        unit.setUnitType(type);
        unit.setReservationFee(25000.0);
        unit.setEquityPeriodMonths(24);
        unit.setMonthlyEquity(15000.0);
        unit.setTotalSellingPrice(price);
        return unit;
    }

    private static class TestPropertyCardRow implements PropertyRepository.PropertyCardRow {
        @Override
        public String getId() {
            return "p1";
        }

        @Override
        public String getName() {
            return "Tower";
        }

        @Override
        public String getBasicDescription() {
            return "Desc";
        }

        @Override
        public String getLocation() {
            return "Cebu";
        }

        @Override
        public Double getPriceRangeMin() {
            return 2000000.0;
        }

        @Override
        public Double getPriceRangeMax() {
            return 2000000.0;
        }

        @Override
        public String getListingTypes() {
            return "RFO";
        }

        @Override
        public Boolean getHasPromo() {
            return false;
        }

        @Override
        public String getCoverPhotoUrl() {
            return null;
        }
    }
}
