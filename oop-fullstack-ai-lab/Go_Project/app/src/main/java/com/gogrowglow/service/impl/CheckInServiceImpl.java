package com.gogrowglow.service.impl;

import com.gogrowglow.dto.request.CheckInRequest;
import com.gogrowglow.dto.response.CheckInResponse;
import com.gogrowglow.entity.DailyCheckIn;
import com.gogrowglow.entity.User;
import com.gogrowglow.exception.BadRequestException;
import com.gogrowglow.exception.ResourceNotFoundException;
import com.gogrowglow.repository.DailyCheckInRepository;
import com.gogrowglow.repository.UserRepository;
import com.gogrowglow.service.interfaces.CheckInService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final DailyCheckInRepository dailyCheckInRepository;
    private final UserRepository userRepository;

    public CheckInServiceImpl(DailyCheckInRepository dailyCheckInRepository,
                              UserRepository userRepository) {
        this.dailyCheckInRepository = dailyCheckInRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CheckInResponse createCheckIn(String email, CheckInRequest request) {
        User user = fetchUser(email);
        LocalDate today = LocalDate.now();
        if (dailyCheckInRepository.findByUserAndCheckInDate(user, today).isPresent()) {
            throw new BadRequestException("Daily check-in already exists for today.");
        }
        DailyCheckIn checkIn = new DailyCheckIn();
        checkIn.setProductivityLevel(request.getProductivityLevel());
        checkIn.setMoodNote(request.getMoodNote());
        checkIn.setEnergyLevel(request.getEnergyLevel());
        checkIn.setCheckInDate(today);
        checkIn.setUser(user);
        DailyCheckIn saved = dailyCheckInRepository.save(checkIn);
        return mapResponse(saved);
    }

    @Override
    public List<CheckInResponse> getHistory(String email) {
        User user = fetchUser(email);
        return dailyCheckInRepository.findByUserOrderByCheckInDateDesc(user)
                .stream()
                .map(this::mapResponse)
                .collect(Collectors.toList());
    }

    private User fetchUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private CheckInResponse mapResponse(DailyCheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getProductivityLevel(),
                checkIn.getMoodNote(),
                checkIn.getEnergyLevel(),
                checkIn.getCheckInDate()
        );
    }
}
