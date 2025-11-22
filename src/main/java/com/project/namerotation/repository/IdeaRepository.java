package com.project.namerotation.repository;

import com.project.namerotationsystem.model.Idea;
import com.project.namerotationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    
    // Find all ideas ordered by creation date (newest first)
    List<Idea> findAllByOrderByCreatedAtDesc();
    
    // Find ideas by status (ordered by newest first)
    List<Idea> findByStatusOrderByCreatedAtDesc(String status);
    
    // Find ideas by user (ordered by newest first)
    List<Idea> findByUserOrderByCreatedAtDesc(User user);
    
    // Find unviewed ideas (assuming "unviewed" is a specific status)
    List<Idea> findByStatus(String status);
    
    // Count ideas by status
    long countByStatus(String status);
    
    // Find latest ideas for dashboard (top 5 newest)
    List<Idea> findTop5ByOrderByCreatedAtDesc();
    
    // Additional useful methods you might want:
    
    // Find ideas by user and status
    List<Idea> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
    
    // Count ideas by user
    long countByUser(User user);
    
    // Check if user has any ideas
    boolean existsByUser(User user);
}