package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.order.OrderRepository;
import com.is_this_aura_clan.CanteenQ.order.OrderStatus;

@Service
public class StallManagementService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final StallRepository stallRepository;
	private final UserAuthorizationService userAuthorizationService;
	private final OrderRepository orderRepository;

	public StallManagementService(StallRepository stallRepository, UserAuthorizationService userAuthorizationService, OrderRepository orderRepository) {
		this.stallRepository = stallRepository;
		this.userAuthorizationService = userAuthorizationService;
		this.orderRepository = orderRepository;
	}

	@Transactional(readOnly = true)
	public List<StallResponse> listStalls(FirebaseAuthenticationPrincipal principal) {
		requireStaff(principal);
		return stallRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Transactional
	public StallResponse createStall(FirebaseAuthenticationPrincipal principal, StallRequest request) {
		requireStaff(principal);
		assertNameIsAvailable(request.stallName(), null);

		Stall savedStall = stallRepository.save(
			new Stall(request.stallName(), request.vendorName(), request.operatingHours())
		);
		return toResponse(savedStall);
	}

	@Transactional
	public StallResponse updateStall(FirebaseAuthenticationPrincipal principal, UUID stallId, StallRequest request) {
		requireStaff(principal);
		Stall stall = stallRepository.findById(stallId)
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));
		assertNameIsAvailable(request.stallName(), stallId);

		stall.updateDetails(request.stallName(), request.vendorName(), request.operatingHours());
		return toResponse(stallRepository.save(stall));
	}

	@Transactional
	public void deleteStall(FirebaseAuthenticationPrincipal principal, UUID stallId) {
		requireStaff(principal);
		Stall stall = stallRepository.findById(stallId)
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));
		stallRepository.delete(stall);
	}

	private void assertNameIsAvailable(String stallName, UUID stallId) {
		stallRepository.findByStallNameIgnoreCase(stallName)
			.filter(existing -> stallId == null || !existing.getId().equals(stallId))
			.ifPresent(existing -> {
				throw new DuplicateStallException("Stall name already exists: " + existing.getStallName());
			});
	}

	private void requireStaff(FirebaseAuthenticationPrincipal principal) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
	}

	private StallResponse toResponse(Stall stall) {
		int queueLimit = stall.getQueueLimit();
		int queueSlotsLeft = Math.max(0, queueLimit - Math.toIntExact(orderRepository.countByStall_IdAndStatusIn(stall.getId(), ACTIVE_STATUSES)));
		return new StallResponse(
			stall.getId(),
			stall.getStallName(),
			stall.getVendorName(),
			stall.getOperatingHours(),
			queueLimit,
			queueSlotsLeft
		);
	}
}
