package com.project.namerotation.repository;

import com.project.namerotationsystem.model.MonthlyDesire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyDesireRepository extends JpaRepository<MonthlyDesire, Long> {
    
    // Find monthly desire by specific month-year
    Optional<MonthlyDesire> findByMonthYear(LocalDate monthYear);
    
    // Find all monthly desires ordered by month (newest first)
    List<MonthlyDesire> findAllByOrderByMonthYearDesc();
    
    // Find latest monthly desire
    Optional<MonthlyDesire> findTopByOrderByMonthYearDesc();
    
    // Check if monthly desire exists for specific month
    boolean existsByMonthYear(LocalDate monthYear);
    
    // Find current month's desire (using the existing findByMonthYear method)
    default Optional<MonthlyDesire> findCurrentMonthlyDesire(LocalDate currentMonth) {
        return findByMonthYear(currentMonth);
    } // ADDED: Closing brace for the default method
} // ADDED: Closing brace for the class interface