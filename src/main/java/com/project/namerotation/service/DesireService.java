package com.project.namerotation.service;

import com.project.namerotation.dto.DesireDto;
import com.project.namerotation.dto.DesireRequestDto;
import com.project.namerotation.repository.DesireRepository;
import com.project.namerotationsystem.model.Desire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DesireService {

    private final DesireRepository desireRepository;

    @Autowired
    public DesireService(DesireRepository desireRepository) {
        this.desireRepository = desireRepository;
    }

    // Create new desire
    public DesireDto createDesire(DesireRequestDto request) {
        Desire desire = new Desire(request.getDescription(), request.getCategory());
        return convertToDto(desireRepository.save(desire));
    }

    // Get all desires
    public List<DesireDto> getAllDesires() {
        return desireRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get desires by category
    public List<DesireDto> getDesiresByCategory(String category) {
        return desireRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get short-term desires
    public List<DesireDto> getShortTermDesires() {
        return getDesiresByCategory("SHORT_TERM");
    }

    // Get long-term desires
    public List<DesireDto> getLongTermDesires() {
        return getDesiresByCategory("LONG_TERM");
    }

    // Get desire by ID
    public Optional<DesireDto> getDesireById(Long id) {
        return desireRepository.findById(id)
                .map(this::convertToDto);
    }

    // Update desire
    public DesireDto updateDesire(Long id, DesireRequestDto request) {
        Desire desire = desireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Desire not found with id: " + id));
        
        desire.setDescription(request.getDescription());
        desire.setCategory(request.getCategory());
        
        return convertToDto(desireRepository.save(desire));
    }

    // Delete desire
    public void deleteDesire(Long id) {
        if (!desireRepository.existsById(id)) {
            throw new RuntimeException("Desire not found with id: " + id);
        }
        desireRepository.deleteById(id);
    }

    // Get desires count by category
    public long getDesiresCountByCategory(String category) {
        return desireRepository.countByCategory(category);
    }

    // Convert Desire to DesireDto
    private DesireDto convertToDto(Desire desire) {
        return new DesireDto(
                desire.getId(),
                desire.getDescription(),
                desire.getCategory(),
                desire.getCreatedAt(),
                desire.getUpdatedAt()
        );
    }
}