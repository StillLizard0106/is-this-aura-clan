package com.gogrowglow.dto.response;

import com.gogrowglow.entity.enums.ProductivityLevel;

import java.time.LocalDate;

public class CheckInResponse {
    private Long id;
    private ProductivityLevel productivityLevel;
    private String moodNote;
    private Integer energyLevel;
    private LocalDate checkInDate;

    public CheckInResponse(Long id, ProductivityLevel productivityLevel, String moodNote, Integer energyLevel, LocalDate checkInDate) {
        this.id = id;
        this.productivityLevel = productivityLevel;
        this.moodNote = moodNote;
        this.energyLevel = energyLevel;
        this.checkInDate = checkInDate;
    }

    public Long getId() {
        return id;
    }

    public ProductivityLevel getProductivityLevel() {
        return productivityLevel;
    }

    public String getMoodNote() {
        return moodNote;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }
}
