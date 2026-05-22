package com.gogrowglow.service.interfaces;

import com.gogrowglow.dto.request.WellnessRequest;
import com.gogrowglow.dto.response.WellnessResponse;

import java.util.List;

public interface WellnessService {
    List<WellnessResponse> getAllEntries();
    WellnessResponse createEntry(WellnessRequest request);
}
