package com.project.namerotation.service;

import com.project.namerotation.dto.UserDto;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotation.repository.NameRepository;
import com.project.namerotationsystem.model.User;
import com.project.namerotationsystem.model.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NameRepository nameRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, NameRepository nameRepository) {
        this.userRepository = userRepository;
        this.nameRepository = nameRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ===== EXISTING METHODS =====
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all usernames for dropdown
    public List<String> getAllUsernames() {
        return userRepository.findAll().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Create new user with encrypted password
    public User createUser(String username, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        // Set default values for new fields
        user.setFullName(username);
        user.setEmail(username + "@system.com");
        user.setIsActive(true);

        return userRepository.save(user);
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Verify password for login
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Create default admin user if not exists
    public void createDefaultAdminIfNotExists() {
        if (!userRepository.existsByUsername("admin")) {
            createUser("admin", "Admin@123", "ADMIN");
            System.out.println("✅ Default admin user created - Username: admin, Password: Admin@123");
        }
    }

    // ===== NEW METHODS FOR USER MANAGEMENT =====
    
    // Get active users for announcement dropdown (NEW METHOD)
    public List<UserDto> getActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Create user with new name (combined operation)
    @Transactional
    public UserDto createUserWithName(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }
        
        // Create and save the name first
        Name name = new Name();
        name.setName(userDto.getFullName());
        name.setIsActive(true);
        name.setDisplayCount(0);
        Name savedName = nameRepository.save(name);
        
        // Create and save the user
        User user = new User();
        user.setFullName(userDto.getFullName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        user.setName(savedName);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }
    
    // Create user account for existing name
    @Transactional
    public UserDto createUserForExistingName(UserDto userDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }
        
        // Find the existing name
        Name existingName = nameRepository.findById(userDto.getNameId())
                .orElseThrow(() -> new RuntimeException("Name not found with id: " + userDto.getNameId()));
        
        // Check if name already has a user account
        if (userRepository.findUsersWithNames().stream()
                .anyMatch(user -> user.getName() != null && user.getName().getId().equals(existingName.getId()))) {
            throw new RuntimeException("Name already has a user account");
        }
        
        // Create and save the user
        User user = new User();
        user.setFullName(userDto.getFullName() != null ? userDto.getFullName() : existingName.getName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole() != null ? userDto.getRole() : "USER");
        user.setName(existingName);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDto(savedUser);
    }
    
    // Bulk create accounts for existing names
    @Transactional
    public List<UserDto> createBulkAccounts(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(this::createUserForExistingName)
                .collect(Collectors.toList());
    }
    
    // Get all users with their linked names (for admin view)
    public List<UserDto> getAllUsersWithNames() {
        return userRepository.findAllActiveWithNames().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Get user by ID with DTO
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDto(user);
    }
    
    // Update user
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Check if new username is taken by another user
        if (!user.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already taken: " + userDto.getUsername());
        }
        
        // Check if new email is taken by another user
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already taken: " + userDto.getEmail());
        }
        
        user.setFullName(userDto.getFullName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        
        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }
    
    // Delete user (soft delete)
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    // Helper method to convert User to UserDto
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        if (user.getName() != null) {
            dto.setNameId(user.getName().getId());
        }
        return dto;
    }
}