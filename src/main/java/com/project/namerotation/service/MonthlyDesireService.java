package com.project.namerotation.service;

import com.project.namerotation.dto.MonthlyDesireDto;
import com.project.namerotation.dto.MonthlyDesireRequestDto;
import com.project.namerotation.repository.MonthlyDesireRepository;
import com.project.namerotationsystem.model.MonthlyDesire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonthlyDesireService {

    private final MonthlyDesireRepository monthlyDesireRepository;

    @Autowired
    public MonthlyDesireService(MonthlyDesireRepository monthlyDesireRepository) {
        this.monthlyDesireRepository = monthlyDesireRepository;
    }

    // Get current monthly desire
    public Optional<MonthlyDesireDto> getCurrentMonthlyDesire() {
        LocalDate currentMonth = YearMonth.now().atDay(1);
        return monthlyDesireRepository.findByMonthYear(currentMonth)
                .map(this::convertToDto);
    }

    // Create or update monthly desire for current month
    public MonthlyDesireDto createOrUpdateMonthlyDesire(MonthlyDesireRequestDto request) {
        LocalDate currentMonth = YearMonth.now().atDay(1);
        
        Optional<MonthlyDesire> existingDesire = monthlyDesireRepository.findByMonthYear(currentMonth);
        
        MonthlyDesire monthlyDesire;
        if (existingDesire.isPresent()) {
            monthlyDesire = existingDesire.get();
            monthlyDesire.setMessage(request.getMessage());
        } else {
            monthlyDesire = new MonthlyDesire(request.getMessage(), currentMonth);
        }
        
        return convertToDto(monthlyDesireRepository.save(monthlyDesire));
    }

    // Get monthly desire by ID
    public Optional<MonthlyDesireDto> getMonthlyDesireById(Long id) {
        return monthlyDesireRepository.findById(id)
                .map(this::convertToDto);
    }

    // Get all monthly desires
    public List<MonthlyDesireDto> getAllMonthlyDesires() {
        return monthlyDesireRepository.findAllByOrderByMonthYearDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get monthly desire by specific month
    public Optional<MonthlyDesireDto> getMonthlyDesireByMonth(int year, int month) {
        LocalDate monthYear = YearMonth.of(year, month).atDay(1);
        return monthlyDesireRepository.findByMonthYear(monthYear)
                .map(this::convertToDto);
    }

    // Delete monthly desire
    public void deleteMonthlyDesire(Long id) {
        if (!monthlyDesireRepository.existsById(id)) {
            throw new RuntimeException("Monthly desire not found with id: " + id);
        }
        monthlyDesireRepository.deleteById(id);
    }

    // Convert MonthlyDesire to MonthlyDesireDto
    private MonthlyDesireDto convertToDto(MonthlyDesire monthlyDesire) {
        return new MonthlyDesireDto(
                monthlyDesire.getId(),
                monthlyDesire.getMessage(),
                monthlyDesire.getMonthYear(),
                monthlyDesire.getCreatedAt(),
                monthlyDesire.getUpdatedAt()
        );
    }
}