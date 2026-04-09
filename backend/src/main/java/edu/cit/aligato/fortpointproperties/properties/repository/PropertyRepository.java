package edu.cit.aligato.fortpointproperties.properties.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.properties.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    
    // Search by property name (case-insensitive)
    List<Property> findByPropertyNameContainingIgnoreCase(String propertyName);
    
    // Search by location
    List<Property> findByLocationContainingIgnoreCase(String location);
    
    // Search by listing type
    List<Property> findByListingType(String listingType);
    
    // Search by property type
    List<Property> findByPropertyType(String propertyType);
    
    // Find by developer
    List<Property> findByDeveloperName(String developerName);
    
    // Search by price range
    @Query("SELECT p FROM Property p WHERE p.priceRangeMin >= :minPrice AND p.priceRangeMax <= :maxPrice")
    List<Property> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Find pet-friendly properties
    List<Property> findByPetFriendlyTrue();
    
    // Find properties with parking
    List<Property> findByParkingAvailableTrue();
    
    // Check if property name is unique
    boolean existsByPropertyName(String propertyName);
}
