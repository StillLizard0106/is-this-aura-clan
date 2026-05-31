package com.gogrowglow.repository;

import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserAndDueDate(User user, LocalDate dueDate);
    List<Task> findByUser(User user);
}
