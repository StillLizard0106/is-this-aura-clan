package com.gogrowglow.dto.request;

public class WellnessRequest {
    private String date;
    private Integer moodScore;
    private Integer energyScore;
    private Integer stressScore;
    private String notes;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(Integer moodScore) {
        this.moodScore = moodScore;
    }

    public Integer getEnergyScore() {
        return energyScore;
    }

    public void setEnergyScore(Integer energyScore) {
        this.energyScore = energyScore;
    }

    public Integer getStressScore() {
        return stressScore;
    }

    public void setStressScore(Integer stressScore) {
        this.stressScore = stressScore;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
