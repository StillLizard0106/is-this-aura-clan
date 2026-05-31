package com.gogrowglow.dto.response;

public class HabitResponse {
    private Long id;
    private String name;
    private Integer targetCount;
    private String frequency;

    public HabitResponse(Long id, String name, Integer targetCount, String frequency) {
        this.id = id;
        this.name = name;
        this.targetCount = targetCount;
        this.frequency = frequency;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public String getFrequency() {
        return frequency;
    }
}
