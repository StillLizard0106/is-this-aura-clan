package com.gogrowglow.dto.request;

import com.gogrowglow.entity.enums.ProductivityLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CheckInRequest {

    @NotNull
    private ProductivityLevel productivityLevel;

    private String moodNote;

    @Min(1)
    @Max(10)
    private Integer energyLevel;

    public ProductivityLevel getProductivityLevel() {
        return productivityLevel;
    }

    public void setProductivityLevel(ProductivityLevel productivityLevel) {
        this.productivityLevel = productivityLevel;
    }

    public String getMoodNote() {
        return moodNote;
    }

    public void setMoodNote(String moodNote) {
        this.moodNote = moodNote;
    }

    public Integer getEnergyLevel() {
        return energyLevel;
    }

    public void setEnergyLevel(Integer energyLevel) {
        this.energyLevel = energyLevel;
    }
}
