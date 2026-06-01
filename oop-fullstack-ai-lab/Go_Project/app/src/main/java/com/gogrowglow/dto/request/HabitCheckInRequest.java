package com.gogrowglow.dto.request;

import jakarta.validation.constraints.NotNull;

public class HabitCheckInRequest {

    @NotNull
    private Long habitId;

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }
}
