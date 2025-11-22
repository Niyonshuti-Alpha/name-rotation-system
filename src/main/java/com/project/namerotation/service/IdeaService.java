package com.project.namerotation.service;

import com.project.namerotation.dto.IdeaDto;
import com.project.namerotation.dto.IdeaRequestDto;
import com.project.namerotation.repository.IdeaRepository;
import com.project.namerotation.repository.UserRepository;
import com.project.namerotationsystem.model.Idea;
import com.project.namerotationsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository, UserRepository userRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
    }

    // Submit new idea
    public Idea submitIdea(Long userId, IdeaRequestDto ideaRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Idea idea = new Idea(user, ideaRequest.getTitle(), ideaRequest.getContent());
        return ideaRepository.save(idea);
    }

    // Get all ideas (for admin)
    public List<IdeaDto> getAllIdeas() {
        return ideaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get ideas by user
    public List<IdeaDto> getUserIdeas(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return ideaRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get idea by ID
    public Optional<IdeaDto> getIdeaById(Long id) {
        return ideaRepository.findById(id)
                .map(this::convertToDto);
    }

    // Mark idea as viewed
    public IdeaDto markAsViewed(Long id) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        
        idea.setStatus("VIEWED");
        idea.setViewedAt(LocalDateTime.now());
        
        return convertToDto(ideaRepository.save(idea));
    }

    // Respond to idea
    public IdeaDto respondToIdea(Long id, String response) {
        Idea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Idea not found with id: " + id));
        
        idea.setStatus("RESPONDED");
        idea.setAdminResponse(response);
        idea.setRespondedAt(LocalDateTime.now());
        
        return convertToDto(ideaRepository.save(idea));
    }

    // Get pending ideas count (for admin notifications)
    public long getPendingIdeasCount() {
        return ideaRepository.countByStatus("PENDING");
    }

    // Get latest ideas for dashboard
    public List<IdeaDto> getLatestIdeas() {
        return ideaRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Convert Idea to IdeaDto
    private IdeaDto convertToDto(Idea idea) {
        return new IdeaDto(
                idea.getId(),
                idea.getUser().getUsername(),
                idea.getTitle(),
                idea.getContent(),
                idea.getStatus(),
                idea.getAdminResponse(),
                idea.getCreatedAt(),
                idea.getViewedAt(),
                idea.getRespondedAt()
        );
    }
}