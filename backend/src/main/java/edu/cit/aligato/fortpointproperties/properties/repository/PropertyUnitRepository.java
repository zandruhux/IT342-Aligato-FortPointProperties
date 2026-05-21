package edu.cit.aligato.fortpointproperties.properties.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.properties.entity.PropertyUnit;

@Repository
public interface PropertyUnitRepository extends JpaRepository<PropertyUnit, String> {
    List<PropertyUnit> findByProperty_Id(String propertyId);
    void deleteByProperty_Id(String propertyId);
}
