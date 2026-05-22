package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.WellnessRequest;
import com.gogrowglow.dto.response.WellnessResponse;
import com.gogrowglow.entity.WellnessEntry;
import com.gogrowglow.mapper.ProductivityMapper;
import com.gogrowglow.repository.WellnessRepository;
import com.gogrowglow.service.interfaces.WellnessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WellnessServiceImpl implements WellnessService {

    private final WellnessRepository wellnessRepository;

    public WellnessServiceImpl(WellnessRepository wellnessRepository) {
        this.wellnessRepository = wellnessRepository;
    }

    @Override
    public List<WellnessResponse> getAllEntries() {
        return wellnessRepository.findAll().stream()
                .map(ProductivityMapper::toWellnessResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WellnessResponse createEntry(WellnessRequest request) {
        WellnessEntry entry = ProductivityMapper.toWellnessEntry(request);
        WellnessEntry saved = wellnessRepository.save(entry);
        return ProductivityMapper.toWellnessResponse(saved);
    }
}
