package com.gogrowglow.repository;

import com.gogrowglow.entity.WellnessEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WellnessRepository extends JpaRepository<WellnessEntry, Long> {
}
