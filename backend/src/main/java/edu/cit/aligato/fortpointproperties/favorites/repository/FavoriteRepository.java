package edu.cit.aligato.fortpointproperties.favorites.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.favorites.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    // Find all favorites for a specific user, ordered by most recent
    List<Favorite> findByUserIdOrderByCreatedAtDesc(String userId);

    // Check if a user has favorited a specific property
    Optional<Favorite> findByUserIdAndPropertyId(String userId, String propertyId);

    // Check if favorite exists (for convenience)
    boolean existsByUserIdAndPropertyId(String userId, String propertyId);

    // Get count of favorites for a user
    long countByUserId(String userId);

    // Get count of how many times a property was favorited
    long countByPropertyId(String propertyId);

    // Delete favorites tied to a property before deleting the property itself
    void deleteByPropertyId(String propertyId);
}
