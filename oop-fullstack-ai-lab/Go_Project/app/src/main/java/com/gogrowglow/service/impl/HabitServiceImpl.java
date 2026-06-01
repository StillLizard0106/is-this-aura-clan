package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.HabitCheckInRequest;
import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitHistoryResponse;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.entity.HabitLog;
import com.gogrowglow.entity.User;
import com.gogrowglow.exception.BadRequestException;
import com.gogrowglow.exception.ResourceNotFoundException;
import com.gogrowglow.repository.HabitLogRepository;
import com.gogrowglow.repository.HabitRepository;
import com.gogrowglow.repository.UserRepository;
import com.gogrowglow.service.interfaces.HabitService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    public HabitServiceImpl(HabitRepository habitRepository,
                            HabitLogRepository habitLogRepository,
                            UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<HabitResponse> getHabits(String email) {
        User user = fetchUser(email);
        return habitRepository.findByUser(user)
                .stream()
                .map(this::mapHabit)
                .collect(Collectors.toList());
    }

    @Override
    public HabitResponse createHabit(String email, HabitRequest request) {
        User user = fetchUser(email);
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setTargetCount(request.getTargetCount());
        habit.setFrequency(request.getFrequency());
        habit.setUser(user);
        Habit saved = habitRepository.save(habit);
        return mapHabit(saved);
    }

    @Override
    public HabitHistoryResponse checkInHabit(String email, HabitCheckInRequest request) {
        User user = fetchUser(email);
        Habit habit = findHabitByIdAndOwner(request.getHabitId(), user);
        LocalDate today = LocalDate.now();
        if (habitLogRepository.existsByHabitAndCompletedDate(habit, today)) {
            throw new BadRequestException("Habit check-in already recorded for today.");
        }
        HabitLog habitLog = new HabitLog();
        habitLog.setHabit(habit);
        habitLog.setCompletedDate(today);
        habitLog.setStatus("COMPLETED");
        HabitLog saved = habitLogRepository.save(habitLog);
        return new HabitHistoryResponse(saved.getCompletedDate(), saved.getStatus());
    }

    @Override
    public List<HabitHistoryResponse> getHabitHistory(String email, Long habitId) {
        User user = fetchUser(email);
        Habit habit = findHabitByIdAndOwner(habitId, user);
        return habitLogRepository.findByHabitOrderByCompletedDateDesc(habit)
                .stream()
                .map(log -> new HabitHistoryResponse(log.getCompletedDate(), log.getStatus()))
                .collect(Collectors.toList());
    }

    private User fetchUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Habit findHabitByIdAndOwner(Long habitId, User user) {
        return habitRepository.findById(habitId)
                .filter(habit -> habit.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Habit not found."));
    }

    private HabitResponse mapHabit(Habit habit) {
        return new HabitResponse(habit.getId(), habit.getName(), habit.getTargetCount(), habit.getFrequency());
    }
}
