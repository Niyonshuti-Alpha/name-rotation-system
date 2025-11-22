package com.project.namerotation.service;

import com.project.namerotation.repository.TaskAssignmentRepository;
import com.project.namerotationsystem.model.Name;
import com.project.namerotationsystem.model.TaskAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class TaskService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NameService nameService;

    @Autowired
    public TaskService(TaskAssignmentRepository taskAssignmentRepository, NameService nameService) {
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.nameService = nameService;
    }

    // Generate task assignments for a given number of names with enhanced rotational algorithm
    @Transactional
    public void generateTasks(int numberOfNames) {
        if (numberOfNames < 4) {
            throw new RuntimeException("Number of names must be at least 4 (for special task selection)");
        }

        // Get active names count
        long activeNamesCount = nameService.getActiveNamesCount();
        if (activeNamesCount < numberOfNames) {
            throw new RuntimeException("Not enough active names. Available: " + activeNamesCount + ", Requested: " + numberOfNames);
        }

        // Clear existing tasks for today
        LocalDate today = LocalDate.now();
        taskAssignmentRepository.deleteBySessionDate(today);

        // NEW: Enhanced rotational selection
        List<Name> selectedNames = getNamesRotationally(numberOfNames);
        
        // Create normal task assignments
        for (Name name : selectedNames) {
            TaskAssignment task = new TaskAssignment();
            task.setName(name);
            task.setTaskName(""); // Empty initially, admin will fill
            task.setIsSpecialTask(false);
            task.setSessionDate(today);
            taskAssignmentRepository.save(task);

            // Update last displayed date for the name
            nameService.updateLastDisplayed(name);
        }

        // Randomly select 4 names for special tasks
        List<Name> shuffledNames = new ArrayList<>(selectedNames);
        Collections.shuffle(shuffledNames);
        List<Name> specialNames = shuffledNames.subList(0, Math.min(4, shuffledNames.size()));

        // Create special task assignments
        for (Name name : specialNames) {
            TaskAssignment specialTask = new TaskAssignment();
            specialTask.setName(name);
            specialTask.setTaskName(null); // No task name for special tasks
            specialTask.setIsSpecialTask(true);
            specialTask.setSessionDate(today);
            taskAssignmentRepository.save(specialTask);
        }
    }

    // NEW: Enhanced rotational algorithm
    private List<Name> getNamesRotationally(int namesNeeded) {
        List<Name> allActiveNames = nameService.getAllActiveNames();
        
        if (allActiveNames.size() <= namesNeeded) {
            // If we have equal or fewer names than needed, return all
            return allActiveNames;
        }
        
        // Sort by last displayed date (oldest first) for fairness
        List<Name> sortedNames = nameService.getNamesOrderedByLastDisplayed();
        
        // Select the required number of names
        List<Name> selectedNames = new ArrayList<>();
        
        // First, take names that haven't been assigned recently
        for (Name name : sortedNames) {
            if (selectedNames.size() >= namesNeeded) break;
            selectedNames.add(name);
        }
        
        // If we still need more names, include ones that were assigned longer ago
        if (selectedNames.size() < namesNeeded) {
            int remaining = namesNeeded - selectedNames.size();
            // Get names that were not selected in the first pass
            List<Name> remainingNames = new ArrayList<>(allActiveNames);
            remainingNames.removeAll(selectedNames);
            
            // Sort remaining names by last displayed date (oldest first)
            remainingNames.sort(Comparator.comparing(name -> 
                name.getLastDisplayedDate() != null ? name.getLastDisplayedDate() : LocalDate.MIN
            ));
            
            // Add the oldest remaining names
            for (int i = 0; i < Math.min(remaining, remainingNames.size()); i++) {
                selectedNames.add(remainingNames.get(i));
            }
        }
        
        return selectedNames;
    }

    // Get normal tasks for today
    public List<TaskAssignment> getNormalTasks() {
        LocalDate today = LocalDate.now();
        return taskAssignmentRepository.findBySessionDateAndIsSpecialTaskFalse(today);
    }

    // Get special tasks for today
    public List<TaskAssignment> getSpecialTasks() {
        LocalDate today = LocalDate.now();
        return taskAssignmentRepository.findBySessionDateAndIsSpecialTaskTrue(today);
    }

    // Get all tasks for today
    public List<TaskAssignment> getAllTasksForToday() {
        LocalDate today = LocalDate.now();
        return taskAssignmentRepository.findBySessionDate(today);
    }

    // NEW: Enhanced update task with automatic sync between normal and special tasks
    @Transactional
    public TaskAssignment updateTask(Long taskId, String taskName, Long newNameId) {
        TaskAssignment task = taskAssignmentRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        Name oldName = task.getName(); // Store the old name for sync

        // Update task name if provided
        if (taskName != null) {
            task.setTaskName(taskName);
        }

        // Replace name if provided
        if (newNameId != null) {
            Name newName = nameService.getNameById(newNameId)
                    .orElseThrow(() -> new RuntimeException("Name not found with id: " + newNameId));
            task.setName(newName);

            // NEW: Sync with special tasks - if the old name exists in special tasks, replace it there too
            syncNameInSpecialTasks(oldName, newName, task.getSessionDate());
        }

        return taskAssignmentRepository.save(task);
    }

    // NEW: Sync name replacement in special tasks
    private void syncNameInSpecialTasks(Name oldName, Name newName, LocalDate sessionDate) {
        // Find if the old name exists in special tasks for the same session date
        List<TaskAssignment> specialTasks = taskAssignmentRepository.findBySessionDateAndIsSpecialTaskTrue(sessionDate);
        
        for (TaskAssignment specialTask : specialTasks) {
            if (specialTask.getName().getId().equals(oldName.getId())) {
                // Replace the name in special task
                specialTask.setName(newName);
                taskAssignmentRepository.save(specialTask);
                break; // Assuming a name appears only once in special tasks
            }
        }
    }

    // Save all task changes
    public void saveAllTasks(List<TaskAssignment> tasks) {
        taskAssignmentRepository.saveAll(tasks);
    }

    // Clear all tasks for today
    @Transactional
    public void clearTodayTasks() {
        LocalDate today = LocalDate.now();
        taskAssignmentRepository.deleteBySessionDate(today);
    }

    // Get latest session date
    public LocalDate getLatestSessionDate() {
        return taskAssignmentRepository.findLatestSessionDate();
    }

    // Check if tasks exist for today
    public boolean hasTasksForToday() {
        LocalDate today = LocalDate.now();
        return !taskAssignmentRepository.findBySessionDate(today).isEmpty();
    }
}