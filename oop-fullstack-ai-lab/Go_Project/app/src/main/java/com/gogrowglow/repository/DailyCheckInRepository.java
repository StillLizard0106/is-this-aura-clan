package com.gogrowglow.repository;

import com.gogrowglow.entity.DailyCheckIn;
import com.gogrowglow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckInRepository extends JpaRepository<DailyCheckIn, Long> {
    Optional<DailyCheckIn> findByUserAndCheckInDate(User user, LocalDate checkInDate);
    List<DailyCheckIn> findByUserOrderByCheckInDateDesc(User user);
}
