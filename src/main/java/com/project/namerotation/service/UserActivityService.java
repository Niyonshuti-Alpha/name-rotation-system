package com.project.namerotation.service;

import com.project.namerotation.dto.UserActivityDto;
import com.project.namerotation.repository.UserActivityRepository;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotationsystem.model.User;
import com.project.namerotationsystem.model.UserActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserActivityService(UserActivityRepository userActivityRepository, UserRepository userRepository) {
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
    }

    // Record user visit
    public void recordUserVisit(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Optional<UserActivity> existingActivity = userActivityRepository.findByUser(user);
        
        if (existingActivity.isPresent()) {
            UserActivity activity = existingActivity.get();
            activity.incrementVisitCount();
            userActivityRepository.save(activity);
        } else {
            UserActivity newActivity = new UserActivity(user);
            userActivityRepository.save(newActivity);
        }
    }

    // Get all user activities
    public List<UserActivityDto> getAllUserActivities() {
        return userActivityRepository.findAllByOrderByLastVisitDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get user activity by user ID
    public Optional<UserActivityDto> getUserActivity(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return userActivityRepository.findByUser(user)
                .map(this::convertToDto);
    }

    // Get users who haven't visited recently
    public List<UserActivityDto> getInactiveUsers(int daysThreshold) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysThreshold);
        return userActivityRepository.findByLastVisitBefore(thresholdDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get users who haven't visited at all
    public List<UserActivityDto> getNeverVisitedUsers() {
        return userActivityRepository.findByVisitCount(0).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get top visitors
    public List<UserActivityDto> getTopVisitors() {
        return userActivityRepository.findTop10ByOrderByVisitCountDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get total visit statistics
    public UserActivityStats getVisitStatistics() {
        Long totalVisits = userActivityRepository.getTotalVisitCount();
        Double averageVisits = userActivityRepository.getAverageVisitsPerUser();
        long totalUsers = userActivityRepository.count();
        
        return new UserActivityStats(
                totalVisits != null ? totalVisits : 0L,
                averageVisits != null ? averageVisits : 0.0,
                totalUsers
        );
    }

    // Statistics DTO
    public static class UserActivityStats {
        private final Long totalVisits;
        private final Double averageVisits;
        private final Long totalUsers;

        public UserActivityStats(Long totalVisits, Double averageVisits, Long totalUsers) {
            this.totalVisits = totalVisits;
            this.averageVisits = averageVisits;
            this.totalUsers = totalUsers;
        }

        public Long getTotalVisits() { return totalVisits; }
        public Double getAverageVisits() { return averageVisits; }
        public Long getTotalUsers() { return totalUsers; }
    }

    // Convert UserActivity to UserActivityDto
    private UserActivityDto convertToDto(UserActivity userActivity) {
        return new UserActivityDto(
                userActivity.getId(),
                userActivity.getUser().getUsername(),
                userActivity.getVisitCount(),
                userActivity.getLastVisit(),
                userActivity.getCreatedAt()
        );
    }
}