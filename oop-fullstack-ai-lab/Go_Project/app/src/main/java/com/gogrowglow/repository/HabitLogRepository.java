package com.gogrowglow.repository;

import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByHabitOrderByCompletedDateDesc(Habit habit);
    boolean existsByHabitAndCompletedDate(Habit habit, LocalDate completedDate);
}
