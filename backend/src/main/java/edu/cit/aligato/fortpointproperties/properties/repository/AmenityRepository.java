package edu.cit.aligato.fortpointproperties.properties.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.properties.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, String> {
    interface AmenityUsageRow {
        String getId();
        String getName();
        Boolean getDefaultAmenity();
        Long getUsageCount();
    }

    Optional<Amenity> findByNameIgnoreCase(String name);

    List<Amenity> findByDefaultAmenityTrueOrderByNameAsc();

    List<Amenity> findAllByOrderByNameAsc();

    @Query(value = """
            SELECT
                a.id AS id,
                a.name AS name,
                a.default_amenity AS "defaultAmenity",
                COUNT(pa.property_id) AS "usageCount"
            FROM amenities a
            LEFT JOIN property_amenities pa ON pa.amenity_id = a.id
            GROUP BY a.id, a.name, a.default_amenity
            ORDER BY COUNT(pa.property_id) DESC, LOWER(a.name) ASC
            """, nativeQuery = true)
    List<AmenityUsageRow> findAllWithUsageCounts();

    @Query(value = """
            SELECT
                a.id AS id,
                a.name AS name,
                a.default_amenity AS "defaultAmenity",
                COUNT(pa.property_id) AS "usageCount"
            FROM amenities a
            LEFT JOIN property_amenities pa ON pa.amenity_id = a.id
            WHERE a.default_amenity = true
            GROUP BY a.id, a.name, a.default_amenity
            ORDER BY COUNT(pa.property_id) DESC, LOWER(a.name) ASC
            """, nativeQuery = true)
    List<AmenityUsageRow> findDefaultAmenitiesWithUsageCounts();
}
