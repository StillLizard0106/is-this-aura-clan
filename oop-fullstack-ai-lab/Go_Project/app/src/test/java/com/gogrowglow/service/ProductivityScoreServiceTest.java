package com.gogrowglow.service;

import com.gogrowglow.entity.DailyCheckIn;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.HabitLog;
import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.enums.ProductivityLevel;
import com.gogrowglow.entity.enums.TaskStatus;
import com.gogrowglow.entity.enums.TaskPriority;
import com.gogrowglow.service.impl.ProductivityScoreService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductivityScoreServiceTest {

    private final ProductivityScoreService scoreService = new ProductivityScoreService();

    @Test
    void calculateScore_returnsBalancedScore_whenTasksAndHabitsComplete() {
        Task task = new Task();
        task.setStatus(TaskStatus.COMPLETED);
        task.setPriority(TaskPriority.MEDIUM);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setDueDate(LocalDate.now());
        task.setUser(null);
        task.setStatus(TaskStatus.COMPLETED);
        task.markCompleted();

        Habit habit = new Habit();
        habit.setName("Morning stretch");
        habit.setFrequency("DAILY");
        habit.setTargetCount(1);

        HabitLog log = new HabitLog();
        log.setHabit(habit);
        log.setCompletedDate(LocalDate.now());
        log.setStatus("COMPLETED");

        DailyCheckIn checkIn = new DailyCheckIn();
        checkIn.setProductivityLevel(ProductivityLevel.GREEN);
        checkIn.setMoodNote("Good day");
        checkIn.setEnergyLevel(8);
        checkIn.setCheckInDate(LocalDate.now());
        checkIn.setUser(null);

        int score = scoreService.calculateScore(List.of(task), List.of(habit), List.of(log), checkIn);
        assertThat(score).isGreaterThanOrEqualTo(70);
        assertThat(score).isLessThanOrEqualTo(100);
    }

    @Test
    void calculateScore_returnsDefaultWhenNoCompletedItems() {
        int score = scoreService.calculateScore(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null);
        assertThat(score).isBetween(50, 90);
    }
}
