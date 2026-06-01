package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    List<TaskResponse> getTodayTasks(String email);
    TaskResponse createTask(String email, TaskRequest request);
    TaskResponse updateTask(String email, Long taskId, TaskRequest request);
    void deleteTask(String email, Long taskId);
    TaskResponse completeTask(String email, Long taskId);
}
