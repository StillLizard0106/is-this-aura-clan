package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.CheckInRequest;
import com.gogrowglow.dto.response.CheckInResponse;

import java.util.List;

public interface CheckInService {
    CheckInResponse createCheckIn(String email, CheckInRequest request);
    List<CheckInResponse> getHistory(String email);
}
