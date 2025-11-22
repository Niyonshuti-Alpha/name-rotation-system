package com.project.namerotation.repository;

import com.project.namerotationsystem.model.Name;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NameRepository extends JpaRepository<Name, Long> {
    
    // Find all active names
    List<Name> findByIsActiveTrue();
    
    // Find names ordered by last displayed date (oldest first) for fair rotation
    @Query("SELECT n FROM Name n WHERE n.isActive = true ORDER BY n.lastDisplayedDate ASC NULLS FIRST")
    List<Name> findAllActiveOrderedByLastDisplayedDate();
    
    // Find names that have never been displayed
    @Query("SELECT n FROM Name n WHERE n.isActive = true AND n.lastDisplayedDate IS NULL")
    List<Name> findNeverDisplayedNames();
    
    // Find a specific name by name string
    List<Name> findByName(String name);
 // Find names without user accounts
    @Query("SELECT n FROM Name n WHERE n.isActive = true AND n.id NOT IN (SELECT u.name.id FROM User u WHERE u.name IS NOT NULL)")
    List<Name> findNamesWithoutUserAccounts();

	long countByIsActiveTrue();
}
