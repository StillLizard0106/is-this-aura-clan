package com.gogrowglow.dto.response;

import java.time.LocalDate;

public class HabitHistoryResponse {
    private LocalDate completedDate;
    private String status;

    public HabitHistoryResponse(LocalDate completedDate, String status) {
        this.completedDate = completedDate;
        this.status = status;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public String getStatus() {
        return status;
    }
}
