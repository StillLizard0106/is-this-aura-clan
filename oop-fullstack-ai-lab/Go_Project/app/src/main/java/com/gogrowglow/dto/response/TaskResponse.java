package com.gogrowglow.dto.response;

import com.gogrowglow.entity.enums.TaskPriority;
import com.gogrowglow.entity.enums.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate dueDate;
    private Instant completedAt;
    private Instant createdAt;

    public TaskResponse(Long id, String title, String description, TaskPriority priority, TaskStatus status, LocalDate dueDate, Instant completedAt, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
