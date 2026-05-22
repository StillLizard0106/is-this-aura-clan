package com.gogrowglow.service.impl;

import com.gogrowglow.dto.response.DashboardResponse;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.WellnessEntry;
import com.gogrowglow.repository.HabitRepository;
import com.gogrowglow.repository.TaskRepository;
import com.gogrowglow.repository.WellnessRepository;
import com.gogrowglow.service.interfaces.DashboardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final WellnessRepository wellnessRepository;

    public DashboardServiceImpl(TaskRepository taskRepository,
                                HabitRepository habitRepository,
                                WellnessRepository wellnessRepository) {
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.wellnessRepository = wellnessRepository;
    }

    @Override
    public DashboardResponse getDashboardSummary() {
        List<Task> tasks = taskRepository.findAll();
        List<Habit> habits = habitRepository.findAll();
        List<WellnessEntry> entries = wellnessRepository.findAll();

        DashboardResponse response = new DashboardResponse();
        response.setTotalTasks(tasks.size());
        response.setCompletedTasks((int) tasks.stream().filter(Task::isCompleted).count());
        response.setTotalHabits(habits.size());
        response.setActiveStreaks((int) habits.stream().filter(habit -> habit.getStreak() > 0).count());
        response.setWellnessBalance(calculateWellnessBalance(entries));
        response.setScoreMessage(generateScoreMessage(response));
        return response;
    }

    private double calculateWellnessBalance(List<WellnessEntry> entries) {
        if (entries.isEmpty()) {
            return 0.0;
        }
        double averageMood = entries.stream().mapToInt(WellnessEntry::getMoodScore).average().orElse(0.0);
        double averageEnergy = entries.stream().mapToInt(WellnessEntry::getEnergyScore).average().orElse(0.0);
        double averageStress = entries.stream().mapToInt(WellnessEntry::getStressScore).average().orElse(0.0);
        return Math.round((averageMood + averageEnergy - averageStress) / 3.0 * 10.0) / 10.0;
    }

    private String generateScoreMessage(DashboardResponse response) {
        if (response.getTotalTasks() == 0 && response.getTotalHabits() == 0) {
            return "Welcome! Add your first task, habit, or wellness entry to begin.";
        }
        if (response.getWellnessBalance() >= 5.0) {
            return "You are balancing productivity and wellness well this week.";
        }
        if (response.getWellnessBalance() >= 2.5) {
            return "Steady progress — keep nurturing habits and recovery.";
        }
        return "Focus on rest and consistency. Small steps today matter.";
    }
}
