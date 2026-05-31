package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.response.AnalyticsSummaryResponse;

public interface AnalyticsService {
    AnalyticsSummaryResponse getSummary(String email);
}
