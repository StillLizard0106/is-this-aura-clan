package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.WellnessRequest;
import com.gogrowglow.dto.response.WellnessResponse;
import com.gogrowglow.repository.WellnessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WellnessServiceImplTest {

    @Autowired
    private WellnessRepository wellnessRepository;

    @Autowired
    private WellnessServiceImpl wellnessService;

    @BeforeEach
    void setUp() {
        wellnessRepository.deleteAll();
    }

    @Test
    void createEntry_shouldSaveWellnessScore() {
        WellnessRequest request = new WellnessRequest();
        request.setDate("2026-05-22");
        request.setMoodScore(8);
        request.setEnergyScore(7);
        request.setStressScore(3);

        WellnessResponse response = wellnessService.createEntry(request);

        assertNotNull(response.getId());
        assertEquals(8, response.getMoodScore());
        assertEquals(7, response.getEnergyScore());
    }
}
