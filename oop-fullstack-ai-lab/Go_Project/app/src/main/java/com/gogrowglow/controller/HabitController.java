package com.gogrowglow.controller;

import com.gogrowglow.dto.request.HabitCheckInRequest;
import com.gogrowglow.dto.request.HabitRequest;
import com.gogrowglow.dto.response.HabitHistoryResponse;
import com.gogrowglow.dto.response.HabitResponse;
import com.gogrowglow.service.interfaces.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> getHabits(Authentication authentication) {
        return ResponseEntity.ok(habitService.getHabits(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(Authentication authentication,
                                                     @Valid @RequestBody HabitRequest request) {
        return ResponseEntity.ok(habitService.createHabit(authentication.getName(), request));
    }

    @PatchMapping("/{id}/check-in")
    public ResponseEntity<HabitHistoryResponse> checkInHabit(Authentication authentication,
                                                             @PathVariable Long id) {
        HabitCheckInRequest request = new HabitCheckInRequest();
        request.setHabitId(id);
        return ResponseEntity.ok(habitService.checkInHabit(authentication.getName(), request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HabitHistoryResponse>> getHabitHistory(Authentication authentication,
                                                                      @PathVariable Long id) {
        return ResponseEntity.ok(habitService.getHabitHistory(authentication.getName(), id));
    }
}
