package com.gogrowglow.dto.response;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private List<TaskResponse> todayTasks;
    private Map<String, Object> habitSummary;
    private CheckInResponse dailyCheckIn;
    private Integer productivityScore;
    private List<Map<String, Object>> currentStreaks;

    public DashboardResponse(List<TaskResponse> todayTasks,
                             Map<String, Object> habitSummary,
                             CheckInResponse dailyCheckIn,
                             Integer productivityScore,
                             List<Map<String, Object>> currentStreaks) {
        this.todayTasks = todayTasks;
        this.habitSummary = habitSummary;
        this.dailyCheckIn = dailyCheckIn;
        this.productivityScore = productivityScore;
        this.currentStreaks = currentStreaks;
    }

    public List<TaskResponse> getTodayTasks() {
        return todayTasks;
    }

    public Map<String, Object> getHabitSummary() {
        return habitSummary;
    }

    public CheckInResponse getDailyCheckIn() {
        return dailyCheckIn;
    }

    public Integer getProductivityScore() {
        return productivityScore;
    }

    public List<Map<String, Object>> getCurrentStreaks() {
        return currentStreaks;
    }
}
