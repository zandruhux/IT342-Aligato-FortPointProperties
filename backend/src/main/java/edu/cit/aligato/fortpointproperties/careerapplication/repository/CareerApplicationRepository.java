package edu.cit.aligato.fortpointproperties.careerapplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.auth.entity.User;
import edu.cit.aligato.fortpointproperties.careerapplication.entity.CareerApplication;
import edu.cit.aligato.fortpointproperties.careerapplication.entity.CareerApplicationStatus;

@Repository
public interface CareerApplicationRepository extends JpaRepository<CareerApplication, String> {
    List<CareerApplication> findByStatus(CareerApplicationStatus status);

    List<CareerApplication> findByUserOrderBySubmittedAtDesc(User user);

    Optional<CareerApplication> findFirstByUserOrderBySubmittedAtDesc(User user);

    boolean existsByUserAndStatus(User user, CareerApplicationStatus status);
}
