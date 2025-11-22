package com.project.namerotation.repository;

import com.project.namerotationsystem.model.User;
import com.project.namerotationsystem.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    
    // Find user activity by user
    Optional<UserActivity> findByUser(User user);
    
    // Find all user activities ordered by last visit (most recent first)
    List<UserActivity> findAllByOrderByLastVisitDesc();
    
    // Find users who haven't visited since specific date
    List<UserActivity> findByLastVisitBefore(LocalDateTime date);
    
    // Find users with most visits (top 10)
    List<UserActivity> findTop10ByOrderByVisitCountDesc();
    
    // Find users who haven't visited yet (visitCount = 0)
    List<UserActivity> findByVisitCount(Integer visitCount);
    
    // Get total visit count across all users
    @Query("SELECT SUM(ua.visitCount) FROM UserActivity ua")
    Long getTotalVisitCount();
    
    // Get average visits per user
    @Query("SELECT AVG(ua.visitCount) FROM UserActivity ua")
    Double getAverageVisitsPerUser();
    
    // Check if user activity exists for user
    boolean existsByUser(User user);
    
    // NEW: Delete activities by user (for cascade delete)
    @Modifying
    @Query("DELETE FROM UserActivity ua WHERE ua.user = :user")
    void deleteByUser(@Param("user") User user);
    
    // NEW: Alternative - delete by user ID
    @Modifying
    @Query("DELETE FROM UserActivity ua WHERE ua.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // NEW: Find activities by user ID
    List<UserActivity> findByUserId(Long userId);
    
    // NEW: Count activities by user
    long countByUser(User user);
}