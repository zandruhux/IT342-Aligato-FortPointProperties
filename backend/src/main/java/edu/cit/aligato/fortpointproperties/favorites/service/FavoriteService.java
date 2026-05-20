package edu.cit.aligato.fortpointproperties.favorites.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.favorites.dto.FavoriteDTO;
import edu.cit.aligato.fortpointproperties.favorites.entity.Favorite;
import edu.cit.aligato.fortpointproperties.favorites.repository.FavoriteRepository;
import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyPhoto;
import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;
import edu.cit.aligato.fortpointproperties.properties.repository.PropertyRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, PropertyRepository propertyRepository) {
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
    }

    /**
     * Add a property to user's favorites
     * Returns true if added successfully, false if already exists
     */
    public boolean addToFavorites(User user, String propertyId) {
        // Check if property exists
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        if (!Boolean.TRUE.equals(property.getVisible())) {
            throw new IllegalArgumentException("Property not found");
        }

        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId)) {
            return false; // Already favorited
        }

        // Create and save favorite
        Favorite favorite = new Favorite(user, property);
        favoriteRepository.save(favorite);
        return true;
    }

    /**
     * Remove a property from user's favorites
     * Returns true if removed successfully, false if not found
     */
    public boolean removeFromFavorites(User user, String propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(user.getId(), propertyId)
                .orElse(null);

        if (favorite == null) {
            return false; // Not favorited
        }

        favoriteRepository.delete(favorite);
        return true;
    }

    /**
     * Get all favorites for a user
     */
    public List<FavoriteDTO> getFavoritesByUser(User user) {
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        return favorites.stream()
                .filter(fav -> Boolean.TRUE.equals(fav.getProperty().getVisible()))
                .map(fav -> new FavoriteDTO(
                        fav.getId(),
                        fav.getProperty().getId(),
                        fav.getProperty().getName(),
                        fav.getProperty().getBasicDescription(),
                        fav.getProperty().getLocation(),
                        calculateMinPrice(fav.getProperty()),
                        calculateMaxPrice(fav.getProperty()),
                        fav.getProperty().getHasPromo(),
                        coverPhotoUrl(fav.getProperty()),
                        fav.getCreatedAt()))
                .toList();
    }

    /**
     * Check if a property is favorited by a user
     */
    public boolean isFavorited(User user, String propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId);
    }

    /**
     * Get favorite count for a user
     */
    public long getFavoriteCount(User user) {
        return favoriteRepository.countByUserId(user.getId());
    }

    /**
     * Get how many users favorited a specific property
     */
    public long getPropertyFavoriteCount(String propertyId) {
        return favoriteRepository.countByPropertyId(propertyId);
    }

    private Double calculateMinPrice(Property property) {
        return property.getUnits().stream()
                .map(PropertyUnit::getTotalSellingPrice)
                .filter(java.util.Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);
    }

    private Double calculateMaxPrice(Property property) {
        return property.getUnits().stream()
                .map(PropertyUnit::getTotalSellingPrice)
                .filter(java.util.Objects::nonNull)
                .max(Double::compareTo)
                .orElse(null);
    }

    private String coverPhotoUrl(Property property) {
        return property.getPhotos().stream()
                .sorted(java.util.Comparator.comparing(PropertyPhoto::getDisplayOrder))
                .map(PropertyPhoto::getPhotoUrl)
                .findFirst()
                .orElse(null);
    }
}
