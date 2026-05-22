package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.TaskRequest;
import com.gogrowglow.dto.response.TaskResponse;
import com.gogrowglow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceImplTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_shouldPersistTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Write daily reflection");
        request.setNotes("Capture wins and next steps");
        request.setDueDate("2026-05-22");
        request.setPriority("Medium");

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response.getId());
        assertEquals("Write daily reflection", response.getTitle());
        assertFalse(response.isCompleted());
    }

    @Test
    void getAllTasks_returnsCreatedTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Prepare study plan");
        request.setPriority("High");
        taskService.createTask(request);

        List<TaskResponse> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals("Prepare study plan", tasks.get(0).getTitle());
    }
}
