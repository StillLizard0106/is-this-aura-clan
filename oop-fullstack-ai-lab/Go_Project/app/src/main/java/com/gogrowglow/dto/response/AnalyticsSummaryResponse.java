package com.gogrowglow.dto.response;

import java.util.Map;

public class AnalyticsSummaryResponse {
    private Map<String, Object> summary;

    public AnalyticsSummaryResponse(Map<String, Object> summary) {
        this.summary = summary;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }
}
