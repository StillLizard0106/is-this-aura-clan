package com.gogrowglow.controller;

import com.gogrowglow.dto.response.AnalyticsSummaryResponse;
import com.gogrowglow.service.interfaces.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getSummary(authentication.getName()));
    }
}
