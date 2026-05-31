package com.gogrowglow.controller;

import com.gogrowglow.dto.request.CheckInRequest;
import com.gogrowglow.dto.response.CheckInResponse;
import com.gogrowglow.service.interfaces.CheckInService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/check-ins")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<CheckInResponse> createCheckIn(Authentication authentication,
                                                         @Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(checkInService.createCheckIn(authentication.getName(), request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<CheckInResponse>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(checkInService.getHistory(authentication.getName()));
    }
}
