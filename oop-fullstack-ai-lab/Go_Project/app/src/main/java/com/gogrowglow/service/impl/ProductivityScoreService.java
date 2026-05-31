package com.gogrowglow.service.impl;

import com.gogrowglow.entity.DailyCheckIn;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.HabitLog;
import com.gogrowglow.entity.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductivityScoreService {

    public int calculateScore(List<Task> tasks, List<Habit> habits, List<HabitLog> habitLogs, DailyCheckIn dailyCheckIn) {
        int taskCompletion = calculateTaskCompletion(tasks);
        int habitConsistency = calculateHabitConsistency(habits, habitLogs);
        int wellnessBalance = calculateWellnessBalance(dailyCheckIn);

        double score = taskCompletion * 0.4 + habitConsistency * 0.4 + wellnessBalance * 0.2;
        return (int) Math.round(score);
    }

    private int calculateTaskCompletion(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 80;
        }
        long total = tasks.size();
        long completed = tasks.stream().filter(task -> task.getStatus() == com.gogrowglow.entity.enums.TaskStatus.COMPLETED).count();
        return (int) Math.round((completed * 100.0) / total);
    }

    private int calculateHabitConsistency(List<Habit> habits, List<HabitLog> habitLogs) {
        if (habits.isEmpty()) {
            return 70;
        }
        long totalTargets = habits.size();
        long completedToday = habitLogs.stream().map(HabitLog::getHabit).distinct().count();
        return (int) Math.round((completedToday * 100.0) / totalTargets);
    }

    private int calculateWellnessBalance(DailyCheckIn dailyCheckIn) {
        if (dailyCheckIn == null) {
            return 60;
        }
        switch (dailyCheckIn.getProductivityLevel()) {
            case GREEN:
                return 90;
            case YELLOW:
                return 70;
            default:
                return 50;
        }
    }
}
