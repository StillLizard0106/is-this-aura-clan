package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;

@Service
public class StaffOrderQueueService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final UserAuthorizationService userAuthorizationService;
	private final StallRepository stallRepository;
	private final OrderRepository orderRepository;
	private final StaffOrderResponseMapper staffOrderResponseMapper;

	public StaffOrderQueueService(
		UserAuthorizationService userAuthorizationService,
		StallRepository stallRepository,
		OrderRepository orderRepository,
		StaffOrderResponseMapper staffOrderResponseMapper
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.stallRepository = stallRepository;
		this.orderRepository = orderRepository;
		this.staffOrderResponseMapper = staffOrderResponseMapper;
	}

	@Transactional(readOnly = true)
	public StaffOrderQueueResponse getQueue(FirebaseAuthenticationPrincipal principal, UUID stallId) {
		return getQueue(principal, stallId, null, null);
	}

	@Transactional(readOnly = true)
	public StaffOrderQueueResponse getQueue(
		FirebaseAuthenticationPrincipal principal,
		UUID stallId,
		LocalDate date,
		List<OrderStatus> requestedStatuses
	) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		if (!stallRepository.existsById(stallId)) {
			throw new com.is_this_aura_clan.CanteenQ.catalog.StallNotFoundException("No stall found for id " + stallId);
		}

		// If the caller didn't specify statuses, default to showing only active orders
		// (PENDING, PREPARING, READY). This prevents the queue from being cluttered with
		// completed or cancelled orders by default.
		List<OrderStatus> statuses = requestedStatuses == null || requestedStatuses.isEmpty()
			? ACTIVE_STATUSES
			: List.copyOf(requestedStatuses); // defensive copy: the caller's list may be mutable

		List<CanteenOrder> orders;
		if (date == null) {
			// No date filter: return all active orders for this stall across any day.
    		// Used by the live queue view.
			orders = orderRepository.findByStall_IdAndStatusInOrderByPickupSlotAscQueueNumberAsc(stallId, statuses);
		} else {
			// Date filter: scope results to a single calendar day.
    		// Used when staff review a specific day's queue (e.g. checking tomorrow).
			LocalDateTime start = date.atStartOfDay();
			LocalDateTime end = date.atTime(LocalTime.MAX);
			orders = orderRepository.findByStall_IdAndPickupSlotBetweenAndStatusInOrderByPickupSlotAscQueueNumberAsc(stallId, start, end, statuses);
		}

		return new StaffOrderQueueResponse(
			stallId,
			orders
				.stream()
				.map(staffOrderResponseMapper::toResponse)
				.toList()
		);
	}
}
