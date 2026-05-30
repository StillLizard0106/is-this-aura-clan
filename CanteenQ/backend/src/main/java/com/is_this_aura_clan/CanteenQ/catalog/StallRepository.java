package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StallRepository extends JpaRepository<Stall, UUID> {

	boolean existsByStallNameIgnoreCase(String stallName);

	Optional<Stall> findByStallNameIgnoreCase(String stallName);
}
