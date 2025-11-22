package com.project.namerotation.repository;

import com.project.namerotationsystem.model.Desire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesireRepository extends JpaRepository<Desire, Long> {
    
    // Find all desires ordered by creation date (newest first)
    List<Desire> findAllByOrderByCreatedAtDesc();
    
    // Find desires by category
    List<Desire> findByCategoryOrderByCreatedAtDesc(String category);
    
    // Find desires by category (short-term or long-term)
    List<Desire> findByCategoryOrderByCreatedAt(String category);
    
    // Count desires by category
    long countByCategory(String category);
    
    // Check if desire with same description exists (optional - for uniqueness)
    boolean existsByDescriptionAndCategory(String description, String category);
}