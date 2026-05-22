package com.gogrowglow.dto.response;

public class WellnessResponse {
    private Long id;
    private String date;
    private int moodScore;
    private int energyScore;
    private int stressScore;
    private String notes;

    public WellnessResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }

    public int getEnergyScore() {
        return energyScore;
    }

    public void setEnergyScore(int energyScore) {
        this.energyScore = energyScore;
    }

    public int getStressScore() {
        return stressScore;
    }

    public void setStressScore(int stressScore) {
        this.stressScore = stressScore;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
