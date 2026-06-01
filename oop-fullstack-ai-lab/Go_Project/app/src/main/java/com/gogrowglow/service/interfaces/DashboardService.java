package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboard(String email);
}
