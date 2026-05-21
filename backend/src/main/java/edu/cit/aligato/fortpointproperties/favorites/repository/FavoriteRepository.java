package edu.cit.aligato.fortpointproperties.favorites.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.favorites.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    List<Favorite> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Favorite> findByUserIdAndPropertyId(String userId, String propertyId);

    boolean existsByUserIdAndPropertyId(String userId, String propertyId);

    long countByUserId(String userId);

    long countByPropertyId(String propertyId);

    void deleteByPropertyId(String propertyId);
}
