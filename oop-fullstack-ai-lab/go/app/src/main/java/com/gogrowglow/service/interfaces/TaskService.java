package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    List<TaskResponse> getAllTasks();
    TaskResponse createTask(TaskRequest request);
    TaskResponse markTaskCompleted(Long taskId);
}
