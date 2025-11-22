package com.project.namerotation.service;

import com.project.namerotation.dto.AnnouncementDto;
import com.project.namerotation.dto.AnnouncementRequestDto;
import com.project.namerotation.repository.AnnouncementRepository;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotationsystem.model.Announcement;
import com.project.namerotationsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    // Create new announcement
    public AnnouncementDto createAnnouncement(AnnouncementRequestDto request) {
        Announcement announcement;
        
        if ("SPECIFIC".equals(request.getSendTo()) && request.getSpecificUserId() != null) {
            User specificUser = userRepository.findById(request.getSpecificUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getSpecificUserId()));
            announcement = new Announcement(request.getTitle(), request.getContent(), request.getSendTo(), specificUser);
        } else {
            announcement = new Announcement(request.getTitle(), request.getContent(), request.getSendTo());
        }
        
        return convertToDto(announcementRepository.save(announcement));
    }

    // Get all announcements (for admin)
    public List<AnnouncementDto> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get announcements for specific user (all announcements including expired)
    public List<AnnouncementDto> getUserAnnouncements(Long userId) {
        // Use the fixed repository method that takes userId directly
        return announcementRepository.findAnnouncementsForUser(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get active (non-expired) announcements for specific user
    public List<AnnouncementDto> getActiveUserAnnouncements(Long userId) {
        // Use the fixed repository method that takes userId directly
        return announcementRepository.findActiveAnnouncementsForUser(userId, LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get announcement by ID
    public Optional<AnnouncementDto> getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .map(this::convertToDto);
    }

    // Delete announcement
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new RuntimeException("Announcement not found with id: " + id);
        }
        announcementRepository.deleteById(id);
    }

    // Get active announcements count
    public long getActiveAnnouncementsCount() {
        return announcementRepository.countActiveAnnouncements(LocalDateTime.now());
    }

    // Scheduled task to delete expired announcements (runs daily at 2 AM)
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredAnnouncements() {
        announcementRepository.deleteExpiredAnnouncements(LocalDateTime.now());
        System.out.println("Expired announcements cleaned up at: " + LocalDateTime.now());
    }

    // Convert Announcement to AnnouncementDto
    private AnnouncementDto convertToDto(Announcement announcement) {
        String specificUsername = null;
        if (announcement.getSpecificUser() != null) {
            specificUsername = announcement.getSpecificUser().getUsername();
        }
        
        return new AnnouncementDto(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getSendTo(),
                specificUsername,
                announcement.getCreatedAt(),
                announcement.getExpiresAt()
        );
    }
}