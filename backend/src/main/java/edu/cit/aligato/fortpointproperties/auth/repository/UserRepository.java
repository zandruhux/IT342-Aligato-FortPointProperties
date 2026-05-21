package edu.cit.aligato.fortpointproperties.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u FROM User u
            ORDER BY LOWER(u.firstname), LOWER(u.lastname)
            """)
    List<User> findAllOrderByNameAsc();

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
            ORDER BY LOWER(u.firstname), LOWER(u.lastname)
            """)
    List<User> findByRoleOrderByNameAsc(@Param("role") String role);

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY LOWER(u.firstname), LOWER(u.lastname)
            """)
    List<User> findByNameContainingIgnoreCaseOrderByNameAsc(@Param("keyword") String keyword);

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND (
                  LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY LOWER(u.firstname), LOWER(u.lastname)
            """)
    List<User> findByRoleAndNameContainingIgnoreCaseOrderByNameAsc(@Param("role") String role, @Param("keyword") String keyword);
}
