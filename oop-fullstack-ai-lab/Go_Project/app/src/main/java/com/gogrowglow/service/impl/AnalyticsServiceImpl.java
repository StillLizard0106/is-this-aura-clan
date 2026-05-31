package com.gogrowglow.service.impl;

import com.gogrowglow.dto.response.AnalyticsSummaryResponse;
import com.gogrowglow.entity.DailyCheckIn;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.HabitLog;
import com.gogrowglow.entity.Task;
import com.gogrowglow.entity.User;
import com.gogrowglow.exception.ResourceNotFoundException;
import com.gogrowglow.repository.DailyCheckInRepository;
import com.gogrowglow.repository.HabitLogRepository;
import com.gogrowglow.repository.HabitRepository;
import com.gogrowglow.repository.TaskRepository;
import com.gogrowglow.repository.UserRepository;
import com.gogrowglow.service.interfaces.AnalyticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final DailyCheckInRepository dailyCheckInRepository;

    public AnalyticsServiceImpl(UserRepository userRepository,
                                TaskRepository taskRepository,
                                HabitRepository habitRepository,
                                HabitLogRepository habitLogRepository,
                                DailyCheckInRepository dailyCheckInRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.dailyCheckInRepository = dailyCheckInRepository;
    }

    @Override
    public AnalyticsSummaryResponse getSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        LocalDate weekStart = LocalDate.now().minusDays(6);
        List<Task> tasks = taskRepository.findByUser(user);
        List<Habit> habits = habitRepository.findByUser(user);
        List<HabitLog> habitLogs = habits.stream()
                .flatMap(habit -> habitLogRepository.findByHabitOrderByCompletedDateDesc(habit).stream())
                .collect(Collectors.toList());
        List<DailyCheckIn> checkIns = dailyCheckInRepository.findByUserOrderByCheckInDateDesc(user);

        Map<String, Object> summary = new HashMap<>();
        summary.put("tasksCompletedThisWeek", tasks.stream()
                .filter(task -> task.getCompletedAt() != null)
                .filter(task -> !task.getCompletedAt().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(weekStart))
                .count());
        summary.put("habitConsistencyPercent", calculateHabitConsistency(habits, habitLogs));
        summary.put("streakCount", calculateTotalStreaks(habits));
        summary.put("productivityGaugeTrend", buildProductivityTrend(checkIns));
        return new AnalyticsSummaryResponse(summary);
    }

    private int calculateHabitConsistency(List<Habit> habits, List<HabitLog> logs) {
        if (habits.isEmpty()) {
            return 0;
        }
        long distinctHabits = logs.stream().map(HabitLog::getHabit).distinct().count();
        return (int) Math.round((distinctHabits * 100.0) / habits.size());
    }

    private long calculateTotalStreaks(List<Habit> habits) {
        return habits.stream().mapToLong(this::countHabitStreak).sum();
    }

    private long countHabitStreak(Habit habit) {
        List<HabitLog> logs = habitLogRepository.findByHabitOrderByCompletedDateDesc(habit);
        long streak = 0;
        LocalDate expected = LocalDate.now();
        for (HabitLog log : logs) {
            if (log.getCompletedDate().equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (log.getCompletedDate().isBefore(expected)) {
                break;
            }
        }
        return streak;
    }

    private Map<String, Object> buildProductivityTrend(List<DailyCheckIn> checkIns) {
        Map<String, Object> trend = new HashMap<>();
        long recentCount = checkIns.stream().limit(7).count();
        trend.put("last7Days", checkIns.stream()
                .limit(7)
                .map(checkIn -> Map.of(
                        "date", checkIn.getCheckInDate().toString(),
                        "level", checkIn.getProductivityLevel().name()
                ))
                .collect(Collectors.toList()));
        trend.put("dataPoints", recentCount);
        return trend;
    }
}
