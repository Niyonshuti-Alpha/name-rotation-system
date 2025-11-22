package com.project.namerotation.repository;

import com.project.namerotationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username (for login)
    Optional<User> findByUsername(String username);
    
    // Check if username already exists
    boolean existsByUsername(String username);
    
    // NEW: Find user by name_id (for cascade delete)
    @Query("SELECT u FROM User u WHERE u.name.id = :nameId")
    Optional<User> findByNameId(@Param("nameId") Long nameId);
    
    // NEW METHODS FOR USER MANAGEMENT:
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if email already exists
    boolean existsByEmail(String email);
    
    // Find all active users
    List<User> findByIsActiveTrue();
    
    // Find users by role
    List<User> findByRole(String role);
    
    // Find users with their linked names
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.name WHERE u.isActive = true")
    List<User> findAllActiveWithNames();
    
    // Find users without linked names
    @Query("SELECT u FROM User u WHERE u.name IS NULL AND u.isActive = true")
    List<User> findUsersWithoutNames();
    
    // Find users who have linked names
    @Query("SELECT u FROM User u WHERE u.name IS NOT NULL AND u.isActive = true")
    List<User> findUsersWithNames();
}