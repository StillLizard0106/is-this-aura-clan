package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitResponse;

import java.util.List;

public interface HabitService {
    List<HabitResponse> getAllHabits();
    HabitResponse createHabit(HabitRequest request);
    HabitResponse trackHabit(Long habitId);
}
