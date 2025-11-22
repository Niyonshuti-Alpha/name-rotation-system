package com.project.namerotation.repository;

import com.project.namerotationsystem.model.Announcement;
import com.project.namerotationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    // Find all announcements ordered by creation date (newest first)
    List<Announcement> findAllByOrderByCreatedAtDesc();
    
    // Find announcements by send type
    List<Announcement> findBySendToOrderByCreatedAtDesc(String sendTo);
    
    // NEW VERSION (Fixed):
    // Find announcements for specific user (both ALL and user-specific for THIS user only)
    @Query("SELECT a FROM Announcement a WHERE a.sendTo = 'ALL' OR (a.sendTo = 'SPECIFIC' AND a.specificUser.id = :userId) ORDER BY a.createdAt DESC")
    List<Announcement> findAnnouncementsForUser(@Param("userId") Long userId);
    
    // Find announcements sent to specific user
    List<Announcement> findBySpecificUserOrderByCreatedAtDesc(User user);
    
    // FIXED: Find non-expired announcements for user - using userId instead of User object
    @Query("SELECT a FROM Announcement a WHERE (a.sendTo = 'ALL' OR (a.sendTo = 'SPECIFIC' AND a.specificUser.id = :userId)) AND a.expiresAt > :now ORDER BY a.createdAt DESC")
    List<Announcement> findActiveAnnouncementsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Delete expired announcements
    @Modifying
    @Query("DELETE FROM Announcement a WHERE a.expiresAt < :now")
    void deleteExpiredAnnouncements(@Param("now") LocalDateTime now);
    
    // Count active announcements
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.expiresAt > :now")
    long countActiveAnnouncements(@Param("now") LocalDateTime now);
    
    // Find announcements created after a certain date
    List<Announcement> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime date);
    
    // Find announcements by title containing text (case-insensitive)
    List<Announcement> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
    
    // Find announcements that will expire soon (within next 24 hours)
    @Query("SELECT a FROM Announcement a WHERE a.expiresAt BETWEEN :start AND :end ORDER BY a.expiresAt ASC")
    List<Announcement> findAnnouncementsExpiringBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Find all announcements for admin with pagination support
    @Query("SELECT a FROM Announcement a ORDER BY a.createdAt DESC")
    List<Announcement> findAllForAdmin();
    
    // Count announcements by send type
    long countBySendTo(String sendTo);
    
    // Find latest announcements (limit by count)
    @Query("SELECT a FROM Announcement a ORDER BY a.createdAt DESC LIMIT :count")
    List<Announcement> findLatestAnnouncements(@Param("count") int count);
    
    // NEW: Set specific_user_id to NULL for a user (for cascade delete)
    @Modifying
    @Query("UPDATE Announcement a SET a.specificUser = NULL WHERE a.specificUser.id = :userId")
    void setSpecificUserToNull(@Param("userId") Long userId);
}