package edu.cit.aligato.fortpointproperties.properties.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.properties.entity.Property;
import edu.cit.aligato.fortpointproperties.properties.enums.ListingType;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {

    interface PropertyCardRow {
        String getId();
        String getName();
        String getBasicDescription();
        String getLocation();
        Double getPriceRangeMin();
        Double getPriceRangeMax();
        String getListingTypes();
        Boolean getHasPromo();
        String getCoverPhotoUrl();
    }

    List<Property> findByVisibleTrue();

    List<Property> findByNameContainingIgnoreCase(String name);

    List<Property> findByVisibleTrueAndNameContainingIgnoreCase(String name);

    List<Property> findByLocationContainingIgnoreCase(String location);

    List<Property> findByVisibleTrueAndLocationContainingIgnoreCase(String location);

    List<Property> findByDeveloperContainingIgnoreCase(String developer);

    List<Property> findByPetFriendlyTrue();

    List<Property> findByParkingAvailableTrue();

    boolean existsByName(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    @Query("SELECT DISTINCT p FROM Property p JOIN p.listingTypes lt WHERE lt = :listingType")
    List<Property> findByListingType(@Param("listingType") ListingType listingType);

    @Query("SELECT DISTINCT p FROM Property p JOIN p.listingTypes lt WHERE p.visible = true AND lt = :listingType")
    List<Property> findVisibleByListingType(@Param("listingType") ListingType listingType);

    @Query("""
            SELECT DISTINCT p FROM Property p
            LEFT JOIN p.units u
            WHERE (:includeHidden = true OR p.visible = true)
              AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:developer IS NULL OR LOWER(p.developer) LIKE LOWER(CONCAT('%', :developer, '%')))
              AND (:minPrice IS NULL OR EXISTS (
                    SELECT 1 FROM PropertyUnit uMin
                    WHERE uMin.property = p AND uMin.totalSellingPrice >= :minPrice
              ))
              AND (:maxPrice IS NULL OR EXISTS (
                    SELECT 1 FROM PropertyUnit uMax
                    WHERE uMax.property = p AND uMax.totalSellingPrice <= :maxPrice
              ))
            """)
    List<Property> searchWithFilters(@Param("name") String name,
                                     @Param("location") String location,
                                     @Param("developer") String developer,
                                     @Param("minPrice") Double minPrice,
                                     @Param("maxPrice") Double maxPrice,
                                     @Param("includeHidden") boolean includeHidden);

    @Query(value = """
            SELECT
                p.id AS id,
                p.name AS name,
                p.basic_description AS "basicDescription",
                p.location AS location,
                MIN(pu.total_selling_price) AS "priceRangeMin",
                MAX(pu.total_selling_price) AS "priceRangeMax",
                COALESCE(STRING_AGG(DISTINCT plt.listing_type, ','), '') AS "listingTypes",
                p.has_promo AS "hasPromo",
                (
                    SELECT pp.photo_url
                    FROM property_photos pp
                    WHERE pp.property_id = p.id
                    ORDER BY pp.display_order ASC, pp.id ASC
                    LIMIT 1
                ) AS "coverPhotoUrl"
            FROM properties p
            LEFT JOIN property_units pu ON pu.property_id = p.id
            LEFT JOIN property_listing_types plt ON plt.property_id = p.id
            WHERE (:includeHidden = true OR p.visible = true)
            GROUP BY p.id, p.name, p.basic_description, p.location, p.has_promo, p.featured, p.created_at
            ORDER BY p.featured DESC, p.created_at DESC
            """, nativeQuery = true)
    List<PropertyCardRow> findCardRows(@Param("includeHidden") boolean includeHidden);

    @Query(value = """
            SELECT
                p.id AS id,
                p.name AS name,
                p.basic_description AS "basicDescription",
                p.location AS location,
                MIN(pu.total_selling_price) AS "priceRangeMin",
                MAX(pu.total_selling_price) AS "priceRangeMax",
                COALESCE(STRING_AGG(DISTINCT plt.listing_type, ','), '') AS "listingTypes",
                p.has_promo AS "hasPromo",
                (
                    SELECT pp.photo_url
                    FROM property_photos pp
                    WHERE pp.property_id = p.id
                    ORDER BY pp.display_order ASC, pp.id ASC
                    LIMIT 1
                ) AS "coverPhotoUrl"
            FROM properties p
            LEFT JOIN property_units pu ON pu.property_id = p.id
            LEFT JOIN property_listing_types plt ON plt.property_id = p.id
            WHERE (:includeHidden = true OR p.visible = true)
              AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:developer IS NULL OR LOWER(p.developer) LIKE LOWER(CONCAT('%', :developer, '%')))
              AND (:listingType IS NULL OR EXISTS (
                    SELECT 1 FROM property_listing_types pltFilter
                    WHERE pltFilter.property_id = p.id AND pltFilter.listing_type = :listingType
              ))
            GROUP BY p.id, p.name, p.basic_description, p.location, p.has_promo, p.featured, p.created_at
            HAVING (:minPrice IS NULL OR MAX(pu.total_selling_price) >= :minPrice)
               AND (:maxPrice IS NULL OR MIN(pu.total_selling_price) <= :maxPrice)
            ORDER BY p.featured DESC, p.created_at DESC
            """, nativeQuery = true)
    List<PropertyCardRow> searchCardRows(@Param("name") String name,
                                         @Param("location") String location,
                                         @Param("developer") String developer,
                                         @Param("listingType") String listingType,
                                         @Param("minPrice") Double minPrice,
                                         @Param("maxPrice") Double maxPrice,
                                         @Param("includeHidden") boolean includeHidden);
}
