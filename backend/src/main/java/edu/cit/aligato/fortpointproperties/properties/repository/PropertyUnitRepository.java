package edu.cit.aligato.fortpointproperties.properties.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;

/**
 * PropertyUnitRepository - Data access layer for PropertyUnit entities
 * 
 * Provides CRUD operations and custom query methods for managing property units.
 */
@Repository
public interface PropertyUnitRepository extends JpaRepository<PropertyUnit, String> {

    /**
     * Find all units for a given property
     * @param propertyId the property ID (parent)
     * @return list of PropertyUnit entities for the property
     */
    List<PropertyUnit> findByProperty_Id(String propertyId);

    /**
     * Delete all units for a given property (used during property deletion)
     * @param propertyId the property ID
     */
    void deleteByProperty_Id(String propertyId);
}
