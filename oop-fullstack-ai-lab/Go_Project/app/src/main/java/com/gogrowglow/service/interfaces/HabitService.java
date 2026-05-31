package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.HabitCheckInRequest;
import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitHistoryResponse;
import com.gogrowglow.dto.response.HabitResponse;

import java.util.List;

public interface HabitService {
    List<HabitResponse> getHabits(String email);
    HabitResponse createHabit(String email, HabitRequest request);
    HabitHistoryResponse checkInHabit(String email, HabitCheckInRequest request);
    List<HabitHistoryResponse> getHabitHistory(String email, Long habitId);
}
