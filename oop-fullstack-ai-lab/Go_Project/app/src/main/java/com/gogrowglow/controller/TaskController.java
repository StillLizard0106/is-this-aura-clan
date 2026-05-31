package com.gogrowglow.controller;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.service.interfaces.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getTodayTasks(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(Authentication authentication,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(Authentication authentication,
                                                   @PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        taskService.deleteTask(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.completeTask(authentication.getName(), id));
    }
}
