package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.User;
import com.gogrowglow.exception.BadRequestException;
import com.gogrowglow.exception.ResourceNotFoundException;
import com.gogrowglow.repository.TaskRepository;
import com.gogrowglow.repository.UserRepository;
import com.gogrowglow.service.interfaces.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TaskResponse> getTodayTasks(String email) {
        User user = fetchUser(email);
        return taskRepository.findByUserAndDueDate(user, LocalDate.now())
                .stream()
                .map(this::mapTask)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse createTask(String email, TaskRequest request) {
        User user = fetchUser(email);
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setUser(user);
        Task saved = taskRepository.save(task);
        return mapTask(saved);
    }

    @Override
    public TaskResponse updateTask(String email, Long taskId, TaskRequest request) {
        Task task = fetchOwnedTask(email, taskId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        return mapTask(taskRepository.save(task));
    }

    @Override
    public void deleteTask(String email, Long taskId) {
        Task task = fetchOwnedTask(email, taskId);
        taskRepository.delete(task);
    }

    @Override
    public TaskResponse completeTask(String email, Long taskId) {
        Task task = fetchOwnedTask(email, taskId);
        if (task.getStatus() == com.gogrowglow.entity.enums.TaskStatus.COMPLETED) {
            throw new BadRequestException("Task is already completed.");
        }
        task.markCompleted();
        return mapTask(taskRepository.save(task));
    }

    private User fetchUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Task fetchOwnedTask(String email, Long taskId) {
        User user = fetchUser(email);
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));
    }

    private TaskResponse mapTask(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getCompletedAt(),
                task.getCreatedAt()
        );
    }
}
