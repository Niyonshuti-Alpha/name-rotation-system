package com.project.namerotation.service;

import com.project.namerotation.dto.NameDto;
import com.project.namerotation.repository.NameRepository;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotation.repository.AnnouncementRepository;
import com.project.namerotation.repository.UserActivityRepository;
import com.project.namerotationsystem.model.Name;
import com.project.namerotationsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NameService {
    
    private final NameRepository nameRepository;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserActivityRepository userActivityRepository;
    
    @Autowired
    public NameService(NameRepository nameRepository, UserRepository userRepository, 
                      AnnouncementRepository announcementRepository,
                      UserActivityRepository userActivityRepository) {
        this.nameRepository = nameRepository;
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
        this.userActivityRepository = userActivityRepository;
    }
    
    // Get all names
    public List<Name> getAllNames() {
        return nameRepository.findAll();
    }
    
    // Get all active names
    public List<Name> getAllActiveNames() {
        return nameRepository.findByIsActiveTrue();
    }
    
    // Get names ordered by last displayed date (for fair rotation)
    public List<Name> getNamesOrderedByLastDisplayed() {
        return nameRepository.findAllActiveOrderedByLastDisplayedDate();
    }
    
    // Get names that have never been displayed
    public List<Name> getNeverDisplayedNames() {
        return nameRepository.findNeverDisplayedNames();
    }
    
    // Add new name
    public Name addName(String nameName) {
        if (nameName == null || nameName.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty");
        }
        
        Name name = new Name();
        name.setName(nameName.trim());
        name.setIsActive(true);
        name.setDisplayCount(0);
        
        return nameRepository.save(name);
    }
    
    // Get name by ID
    public Optional<Name> getNameById(Long id) {
        return nameRepository.findById(id);
    }
    
    // Update name
    public Name updateName(Long id, String newName) {
        Name name = nameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Name not found with id: " + id));
        
        name.setName(newName.trim());
        return nameRepository.save(name);
    }
    
    // NEW: Enhanced delete name with complete cascade handling
    @Transactional
    public void deleteName(Long id) {
        try {
            System.out.println("Starting delete process for name ID: " + id);
            
            // 1. Find the name
            Name name = nameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Name not found with id: " + id));
            System.out.println("Found name: " + name.getName());
            
            // 2. Find associated user using the new method
            Optional<User> userOptional = userRepository.findByNameId(id);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Found associated user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                
                // 3. Handle all user dependencies in correct order:
                
                // 3a. Clear user references from announcements
                announcementRepository.setSpecificUserToNull(user.getId());
                System.out.println("Cleared user references from announcements");
                
                // 3b. Delete user activities (NEW)
                userActivityRepository.deleteByUser(user);
                System.out.println("Deleted user activities");
                
                // 3c. Delete the user
                userRepository.delete(user);
                System.out.println("User deleted successfully");
            } else {
                System.out.println("No associated user found for name ID: " + id);
            }
            
            // 4. Delete the name
            nameRepository.delete(name);
            System.out.println("Name deleted successfully");
            
        } catch (Exception e) {
            System.err.println("Error during delete process: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Cannot delete name: " + e.getMessage());
        }
    }
    
    // Update last displayed date and increment display count
    public void updateLastDisplayed(Name name) {
        name.setLastDisplayedDate(LocalDate.now());
        name.setDisplayCount(name.getDisplayCount() + 1);
        nameRepository.save(name);
    }
    
    // Get count of active names
    public long getActiveNamesCount() {
        return nameRepository.findByIsActiveTrue().size();
    }

    public Collection<NameDto> getNamesWithoutUserAccounts() {
        List<Name> names = nameRepository.findNamesWithoutUserAccounts();
        return names.stream()
                .map(name -> new NameDto(name.getId(), name.getName()))
                .collect(Collectors.toList());
    }
}