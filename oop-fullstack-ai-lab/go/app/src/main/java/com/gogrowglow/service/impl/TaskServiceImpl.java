package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.entity.Task;
import com.gogrowglow.mapper.ProductivityMapper;
import com.gogrowglow.repository.TaskRepository;
import com.gogrowglow.service.interfaces.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(ProductivityMapper::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse createTask(TaskRequest request) {
        Task task = ProductivityMapper.toTask(request);
        Task saved = taskRepository.save(task);
        return ProductivityMapper.toTaskResponse(saved);
    }

    @Override
    public TaskResponse markTaskCompleted(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setCompleted(true);
        Task updated = taskRepository.save(task);
        return ProductivityMapper.toTaskResponse(updated);
    }
}
