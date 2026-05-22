package com.gogrowglow.controller;

import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.request.WellnessRequest;
import com.gogrowglow.dto.response.DashboardResponse;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.dto.response.WellnessResponse;
import com.gogrowglow.service.interfaces.DashboardService;
import com.gogrowglow.service.interfaces.HabitService;
import com.gogrowglow.service.interfaces.TaskService;
import com.gogrowglow.service.interfaces.WellnessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductivityController {

    private final TaskService taskService;
    private final HabitService habitService;
    private final WellnessService wellnessService;
    private final DashboardService dashboardService;

    public ProductivityController(TaskService taskService,
                                  HabitService habitService,
                                  WellnessService wellnessService,
                                  DashboardService dashboardService) {
        this.taskService = taskService;
        this.habitService = habitService;
        this.wellnessService = wellnessService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.markTaskCompleted(id));
    }

    @GetMapping("/habits")
    public ResponseEntity<List<HabitResponse>> getHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }

    @PostMapping("/habits")
    public ResponseEntity<HabitResponse> createHabit(@RequestBody HabitRequest request) {
        return ResponseEntity.ok(habitService.createHabit(request));
    }

    @PatchMapping("/habits/{id}/track")
    public ResponseEntity<HabitResponse> trackHabit(@PathVariable Long id) {
        return ResponseEntity.ok(habitService.trackHabit(id));
    }

    @GetMapping("/wellness")
    public ResponseEntity<List<WellnessResponse>> getWellnessEntries() {
        return ResponseEntity.ok(wellnessService.getAllEntries());
    }

    @PostMapping("/wellness")
    public ResponseEntity<WellnessResponse> createWellnessEntry(@RequestBody WellnessRequest request) {
        return ResponseEntity.ok(wellnessService.createEntry(request));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
