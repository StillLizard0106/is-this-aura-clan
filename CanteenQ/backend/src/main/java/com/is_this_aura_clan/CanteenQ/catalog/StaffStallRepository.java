package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;

@Repository
public interface StaffStallRepository extends JpaRepository<StaffStall, UUID> {
	List<StaffStall> findByStaff(UserAccount staff);
	List<StaffStall> findByStall(Stall stall);
	Optional<StaffStall> findByStaffAndStall(UserAccount staff, Stall stall);
	boolean existsByStaffAndStall(UserAccount staff, Stall stall);
}
