package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffStallAssignmentService {

	private final StaffStallRepository staffStallRepository;
	private final UserAccountRepository userAccountRepository;
	private final StallRepository stallRepository;
	private final UserAuthorizationService userAuthorizationService;

	public StaffStallAssignmentService(
		StaffStallRepository staffStallRepository,
		UserAccountRepository userAccountRepository,
		StallRepository stallRepository,
		UserAuthorizationService userAuthorizationService
	) {
		this.staffStallRepository = staffStallRepository;
		this.userAccountRepository = userAccountRepository;
		this.stallRepository = stallRepository;
		this.userAuthorizationService = userAuthorizationService;
	}

	@Transactional(readOnly = true)
	public List<StaffStallAssignmentResponse> getMyStalls(FirebaseAuthenticationPrincipal principal) {
		requireStaff(principal);
		UserAccount staffUser = getUserAccount(principal);
		return staffStallRepository.findByStaff(staffUser).stream()
			.map(this::toResponse)
			.toList();
	}

	@Transactional
	public StaffStallAssignmentResponse assignStall(
		FirebaseAuthenticationPrincipal principal,
		AssignStallRequest request
	) {
		requireStaff(principal);
		UserAccount staffUser = getUserAccount(principal);
		Stall stall = stallRepository.findById(request.stallId())
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + request.stallId()));

		// Check if already assigned
		// Enforce one-to-one: staff may only be assigned to a single stall.
		var existing = staffStallRepository.findByStaff(staffUser);
		if (!existing.isEmpty()) {
			boolean alreadyHere = existing.stream().anyMatch(a -> a.getStall().getId().equals(stall.getId()));
			if (alreadyHere) {
				throw new IllegalArgumentException("Stall already assigned to this staff");
			}
			throw new IllegalArgumentException("You are already assigned to another stall");
		}

		StaffStall assignment = new StaffStall(staffUser, stall);
		StaffStall saved = staffStallRepository.save(assignment);
		return toResponse(saved);
	}

	@Transactional
	public void unassignStall(FirebaseAuthenticationPrincipal principal, UUID stallId) {
		requireStaff(principal);
		UserAccount staffUser = getUserAccount(principal);
		Stall stall = stallRepository.findById(stallId)
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));

		StaffStall assignment = staffStallRepository.findByStaffAndStall(staffUser, stall)
			.orElseThrow(() -> new IllegalArgumentException("This stall is not assigned to you"));

		staffStallRepository.delete(assignment);
	}

	@Transactional(readOnly = true)
	public boolean isStaffAssignedToStall(UserAccount staff, UUID stallId) {
		Stall stall = stallRepository.findById(stallId)
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));
		return staffStallRepository.existsByStaffAndStall(staff, stall);
	}

	private UserAccount getUserAccount(FirebaseAuthenticationPrincipal principal) {
		return userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	private void requireStaff(FirebaseAuthenticationPrincipal principal) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
	}

	private StaffStallAssignmentResponse toResponse(StaffStall assignment) {
		Stall stall = assignment.getStall();
		return new StaffStallAssignmentResponse(
			assignment.getId(),
			stall.getId(),
			stall.getStallName(),
			stall.getVendorName(),
			assignment.getAssignedAt()
		);
	}
}
