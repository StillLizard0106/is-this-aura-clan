package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HabitServiceImplTest {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitServiceImpl habitService;

    @BeforeEach
    void setUp() {
        habitRepository.deleteAll();
    }

    @Test
    void createHabit_shouldStartWithZeroStreak() {
        HabitRequest request = new HabitRequest();
        request.setName("Morning stretch");
        request.setCadence("Daily");

        HabitResponse response = habitService.createHabit(request);

        assertNotNull(response.getId());
        assertEquals("Morning stretch", response.getName());
        assertEquals(0, response.getStreak());
    }

    @Test
    void trackHabit_shouldIncreaseStreak() {
        HabitRequest request = new HabitRequest();
        request.setName("Read a chapter");
        request.setCadence("Daily");
        HabitResponse created = habitService.createHabit(request);

        HabitResponse tracked = habitService.trackHabit(created.getId());

        assertEquals(1, tracked.getStreak());
        assertTrue(tracked.isCompletedToday());
    }
}
