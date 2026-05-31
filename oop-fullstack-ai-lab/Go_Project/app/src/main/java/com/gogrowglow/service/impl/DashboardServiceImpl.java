package com.gogrowglow.service.impl;

import com.gogrowglow.dto.response.CheckInResponse;
import com.gogrowglow.dto.response.DashboardResponse;
import com.gogrowglow.dto.response.TaskResponse;
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
import com.gogrowglow.service.interfaces.DashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final DailyCheckInRepository dailyCheckInRepository;
    private final ProductivityScoreService scoreService;

    public DashboardServiceImpl(UserRepository userRepository,
                                TaskRepository taskRepository,
                                HabitRepository habitRepository,
                                HabitLogRepository habitLogRepository,
                                DailyCheckInRepository dailyCheckInRepository,
                                ProductivityScoreService scoreService) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.dailyCheckInRepository = dailyCheckInRepository;
        this.scoreService = scoreService;
    }

    @Override
    public DashboardResponse getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<Task> tasksDueToday = taskRepository.findByUserAndDueDate(user, LocalDate.now());
        List<Habit> habits = habitRepository.findByUser(user);
        List<HabitLog> todayHabitLogs = habits.stream()
                .flatMap(habit -> habitLogRepository.findByHabitOrderByCompletedDateDesc(habit).stream())
                .filter(log -> log.getCompletedDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
        DailyCheckIn dailyCheckIn = dailyCheckInRepository.findByUserAndCheckInDate(user, LocalDate.now()).orElse(null);

        List<TaskResponse> todayTasks = tasksDueToday.stream().map(this::mapTask).collect(Collectors.toList());
        Map<String, Object> habitSummary = buildHabitSummary(habits, todayHabitLogs);
        CheckInResponse checkInResponse = dailyCheckIn != null ? mapCheckIn(dailyCheckIn) : null;
        int productivityScore = scoreService.calculateScore(tasksDueToday, habits, todayHabitLogs, dailyCheckIn);
        List<Map<String, Object>> currentStreaks = buildCurrentStreaks(habits);

        return new DashboardResponse(todayTasks, habitSummary, checkInResponse, productivityScore, currentStreaks);
    }

    private Map<String, Object> buildHabitSummary(List<Habit> habits, List<HabitLog> todayLogs) {
        Map<String, Object> habitSummary = new HashMap<>();
        habitSummary.put("totalHabits", habits.size());
        habitSummary.put("completedToday", todayLogs.stream().map(log -> log.getHabit().getId()).distinct().count());
        habitSummary.put("consistency", habits.isEmpty() ? 0 : Math.round((todayLogs.stream().map(log -> log.getHabit().getId()).distinct().count() * 100.0f) / habits.size()));
        return habitSummary;
    }

    private List<Map<String, Object>> buildCurrentStreaks(List<Habit> habits) {
        return habits.stream()
                .map(habit -> {
                    Map<String, Object> streak = new HashMap<>();
                    streak.put("habitName", habit.getName());
                    streak.put("streakDays", calculateStreak(habit));
                    return streak;
                })
                .collect(Collectors.toList());
    }

    private long calculateStreak(Habit habit) {
        List<HabitLog> logs = habitLogRepository.findByHabitOrderByCompletedDateDesc(habit);
        LocalDate expectedDate = LocalDate.now();
        long streak = 0;
        for (HabitLog log : logs) {
            if (log.getCompletedDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (log.getCompletedDate().isBefore(expectedDate)) {
                break;
            }
        }
        return streak;
    }

    private TaskResponse mapTask(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getCompletedAt(),
                task.getCreatedAt()
        );
    }

    private CheckInResponse mapCheckIn(DailyCheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getProductivityLevel(),
                checkIn.getMoodNote(),
                checkIn.getEnergyLevel(),
                checkIn.getCheckInDate()
        );
    }
}
