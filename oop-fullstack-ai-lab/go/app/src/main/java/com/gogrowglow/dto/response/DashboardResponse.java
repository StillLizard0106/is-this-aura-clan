package com.gogrowglow.dto.response;

public class DashboardResponse {
    private int totalTasks;
    private int completedTasks;
    private int totalHabits;
    private int activeStreaks;
    private double wellnessBalance;
    private String scoreMessage;

    public DashboardResponse() {
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getTotalHabits() {
        return totalHabits;
    }

    public void setTotalHabits(int totalHabits) {
        this.totalHabits = totalHabits;
    }

    public int getActiveStreaks() {
        return activeStreaks;
    }

    public void setActiveStreaks(int activeStreaks) {
        this.activeStreaks = activeStreaks;
    }

    public double getWellnessBalance() {
        return wellnessBalance;
    }

    public void setWellnessBalance(double wellnessBalance) {
        this.wellnessBalance = wellnessBalance;
    }

    public String getScoreMessage() {
        return scoreMessage;
    }

    public void setScoreMessage(String scoreMessage) {
        this.scoreMessage = scoreMessage;
    }
}
