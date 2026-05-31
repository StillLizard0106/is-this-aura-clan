package com.gogrowglow.repository;

import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
}
