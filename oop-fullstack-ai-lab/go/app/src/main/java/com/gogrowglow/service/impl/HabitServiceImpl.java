package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.entity.Habit;
import com.gogrowglow.mapper.ProductivityMapper;
import com.gogrowglow.repository.HabitRepository;
import com.gogrowglow.service.interfaces.HabitService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;

    public HabitServiceImpl(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Override
    public List<HabitResponse> getAllHabits() {
        return habitRepository.findAll().stream()
                .map(ProductivityMapper::toHabitResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HabitResponse createHabit(HabitRequest request) {
        Habit habit = ProductivityMapper.toHabit(request);
        Habit saved = habitRepository.save(habit);
        return ProductivityMapper.toHabitResponse(saved);
    }

    @Override
    public HabitResponse trackHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));
        LocalDate today = LocalDate.now();
        if (habit.getLastTrackedDate() != null && habit.getLastTrackedDate().equals(today)) {
            return ProductivityMapper.toHabitResponse(habit);
        }

        if (habit.getLastTrackedDate() == null || habit.getLastTrackedDate().plusDays(1).equals(today)) {
            habit.setStreak(habit.getStreak() + 1);
        } else {
            habit.setStreak(1);
        }
        habit.setLastTrackedDate(today);
        Habit updated = habitRepository.save(habit);
        HabitResponse response = ProductivityMapper.toHabitResponse(updated);
        response.setCompletedToday(true);
        return response;
    }
}
