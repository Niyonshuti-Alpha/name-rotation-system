package com.project.namerotation.repository;

import com.project.namerotationsystem.model.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    
    // Find all task assignments for current session
    List<TaskAssignment> findBySessionDate(LocalDate sessionDate);
    
    // Find normal tasks (not special tasks)
    List<TaskAssignment> findBySessionDateAndIsSpecialTaskFalse(LocalDate sessionDate);
    
    // Find special tasks
    List<TaskAssignment> findBySessionDateAndIsSpecialTaskTrue(LocalDate sessionDate);
    
    // Delete all tasks for a specific session
    @Modifying
    @Query("DELETE FROM TaskAssignment t WHERE t.sessionDate = :sessionDate")
    void deleteBySessionDate(LocalDate sessionDate);
    
    // Find latest session date
    @Query("SELECT MAX(t.sessionDate) FROM TaskAssignment t")
    LocalDate findLatestSessionDate();
}

