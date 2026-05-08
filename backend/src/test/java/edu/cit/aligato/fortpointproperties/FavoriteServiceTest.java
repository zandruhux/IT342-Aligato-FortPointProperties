package edu.cit.aligato.fortpointproperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.dto.FavoriteDTO;
import edu.cit.aligato.fortpointproperties.favorites.entity.Favorite;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.favorites.service.FavoriteService;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void addToFavorites_success() {
        User user = new User();
        user.setId("u1");
        Property prop = new Property();
        prop.setId("p1");

        when(propertyRepository.findById("p1")).thenReturn(Optional.of(prop));
        when(favoriteRepository.existsByUserIdAndPropertyId(user.getId(), "p1")).thenReturn(false);

        boolean added = favoriteService.addToFavorites(user, "p1");

        assertTrue(added);
        ArgumentCaptor<Favorite> favoriteCaptor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(favoriteCaptor.capture());
        assertEquals(user, favoriteCaptor.getValue().getUser());
        assertEquals(prop, favoriteCaptor.getValue().getProperty());
    }

    @Test
    void addToFavorites_alreadyFavorited_returnsFalse() {
        User user = new User();
        user.setId("u2");
        Property prop = new Property();
        prop.setId("p2");

        when(propertyRepository.findById("p2")).thenReturn(Optional.of(prop));
        when(favoriteRepository.existsByUserIdAndPropertyId(user.getId(), "p2")).thenReturn(true);

        boolean added = favoriteService.addToFavorites(user, "p2");

        assertFalse(added);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_missingProperty_throwsAndDoesNotSave() {
        User user = new User();
        user.setId("u-missing");

        when(propertyRepository.findById("missing")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> favoriteService.addToFavorites(user, "missing"));

        assertEquals("Property not found", exception.getMessage());
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void removeFromFavorites_success() {
        User user = new User();
        user.setId("u3");
        Favorite fav = new Favorite();
        fav.setId("f1");

        when(favoriteRepository.findByUserIdAndPropertyId(user.getId(), "p3")).thenReturn(Optional.of(fav));

        boolean removed = favoriteService.removeFromFavorites(user, "p3");

        assertTrue(removed);
        verify(favoriteRepository).delete(fav);
    }

    @Test
    void removeFromFavorites_notFound_returnsFalse() {
        User user = new User();
        user.setId("u4");

        when(favoriteRepository.findByUserIdAndPropertyId(user.getId(), "p4")).thenReturn(Optional.empty());

        boolean removed = favoriteService.removeFromFavorites(user, "p4");

        assertFalse(removed);
        verify(favoriteRepository, never()).delete(any(Favorite.class));
    }

    @Test
    void getFavoritesByUser_mapsPropertyDetails() {
        User user = new User();
        user.setId("u5");
        Property prop = new Property();
        prop.setId("p5");
        prop.setName("Tower One");
        prop.setBasicDescription("Near business district");
        prop.setLocation("Cebu City");
        prop.setPriceRangeMin(1500000.0);
        prop.setPriceRangeMax(3500000.0);

        Favorite favorite = new Favorite(user, prop);
        favorite.setId("f5");
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 9, 10, 30);
        favorite.setCreatedAt(createdAt);

        when(favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId())).thenReturn(List.of(favorite));

        List<FavoriteDTO> results = favoriteService.getFavoritesByUser(user);

        assertEquals(1, results.size());
        assertEquals("f5", results.get(0).getId());
        assertEquals("p5", results.get(0).getPropertyId());
        assertEquals("Tower One", results.get(0).getPropertyName());
        assertEquals("Near business district", results.get(0).getDescription());
        assertEquals("Cebu City", results.get(0).getLocation());
        assertEquals(1500000.0, results.get(0).getPriceRangeMin());
        assertEquals(3500000.0, results.get(0).getPriceRangeMax());
        assertEquals(createdAt, results.get(0).getCreatedAt());
    }

    @Test
    void isFavorited_delegatesToRepository() {
        User user = new User();
        user.setId("u6");

        when(favoriteRepository.existsByUserIdAndPropertyId("u6", "p6")).thenReturn(true);

        assertTrue(favoriteService.isFavorited(user, "p6"));
    }

    @Test
    void getFavoriteCount_delegatesToRepository() {
        User user = new User();
        user.setId("u7");

        when(favoriteRepository.countByUserId("u7")).thenReturn(3L);

        assertEquals(3L, favoriteService.getFavoriteCount(user));
    }

    @Test
    void getPropertyFavoriteCount_delegatesToRepository() {
        when(favoriteRepository.countByPropertyId("p8")).thenReturn(4L);

        assertEquals(4L, favoriteService.getPropertyFavoriteCount("p8"));
    }
}
