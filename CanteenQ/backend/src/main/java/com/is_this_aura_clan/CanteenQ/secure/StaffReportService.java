package com.is_this_aura_clan.CanteenQ.secure;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;
import com.is_this_aura_clan.CanteenQ.order.CanteenOrder;
import com.is_this_aura_clan.CanteenQ.order.OrderRepository;
import com.is_this_aura_clan.CanteenQ.order.OrderStatus;

@Service
public class StaffReportService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final UserAuthorizationService userAuthorizationService;
	private final StallRepository stallRepository;
	private final OrderRepository orderRepository;
	private final Clock clock;

	public StaffReportService(
		UserAuthorizationService userAuthorizationService,
		StallRepository stallRepository,
		OrderRepository orderRepository,
		Clock clock
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.stallRepository = stallRepository;
		this.orderRepository = orderRepository;
		this.clock = clock;
	}

	@Transactional(readOnly = true)
	public StaffReportResponse getSummary(FirebaseAuthenticationPrincipal principal, LocalDate startDate, LocalDate endDate) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now(clock);
		LocalDate effectiveEndDate = endDate != null ? endDate : effectiveStartDate;
		LocalDateTime start = effectiveStartDate.atStartOfDay();
		LocalDateTime end = effectiveEndDate.atTime(LocalTime.MAX);
		List<CanteenOrder> ordersInRange = orderRepository.findByCreatedAtBetween(start, end);
		BigDecimal totalRevenue = ordersInRange.stream()
			.map(CanteenOrder::getTotalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
		long preparingOrders = orderRepository.countByStatus(OrderStatus.PREPARING);
		long readyOrders = orderRepository.countByStatus(OrderStatus.READY);
		long activeOrders = pendingOrders + preparingOrders + readyOrders;
		List<StaffStallReportResponse> stallBreakdowns = buildStallBreakdowns(ordersInRange);

		return new StaffReportResponse(
			stallRepository.count(),
			ordersInRange.size(),
			totalRevenue,
			ordersInRange.size(),
			activeOrders,
			pendingOrders,
			preparingOrders,
			readyOrders,
			orderRepository.countByStatusAndUpdatedAtBetween(OrderStatus.COMPLETED, start, end),
			orderRepository.countByStatusAndUpdatedAtBetween(OrderStatus.CANCELLED, start, end),
			orderRepository.countByStatusAndUpdatedAtBetween(OrderStatus.UNCLAIMED, start, end),
			stallBreakdowns
		);
	}

	@Transactional(readOnly = true)
	public List<StaffStallReportResponse> getStallBreakdowns(FirebaseAuthenticationPrincipal principal, LocalDate startDate, LocalDate endDate) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now(clock);
		LocalDate effectiveEndDate = endDate != null ? endDate : effectiveStartDate;
		LocalDateTime start = effectiveStartDate.atStartOfDay();
		LocalDateTime end = effectiveEndDate.atTime(LocalTime.MAX);
		List<CanteenOrder> ordersInRange = orderRepository.findByCreatedAtBetween(start, end);
		return buildStallBreakdowns(ordersInRange);
	}

	private List<StaffStallReportResponse> buildStallBreakdowns(List<CanteenOrder> ordersInRange) {
		Map<java.util.UUID, StallAggregate> aggregates = new LinkedHashMap<>();
		for (Stall stall : stallRepository.findAll()) {
			aggregates.put(stall.getId(), new StallAggregate(stall.getId(), stall.getStallName()));
		}

		for (CanteenOrder order : ordersInRange) {
			StallAggregate aggregate = aggregates.computeIfAbsent(order.getStallId(), stallId -> new StallAggregate(stallId, "Unknown Stall"));
			aggregate.orderCount += 1;
			aggregate.totalRevenue = aggregate.totalRevenue.add(order.getTotalPrice());
		}

		return aggregates.values().stream()
			.sorted(Comparator.comparingLong(StallAggregate::orderCount).reversed().thenComparing(StallAggregate::stallName))
			.map(StallAggregate::toResponse)
			.toList();
	}

	private static final class StallAggregate {
		private final java.util.UUID stallId;
		private final String stallName;
		private long orderCount;
		private BigDecimal totalRevenue = BigDecimal.ZERO;

		private StallAggregate(java.util.UUID stallId, String stallName) {
			this.stallId = stallId;
			this.stallName = stallName;
		}

		private long orderCount() {
			return orderCount;
		}

		private String stallName() {
			return stallName;
		}

		private StaffStallReportResponse toResponse() {
			return new StaffStallReportResponse(stallId, stallName, orderCount, totalRevenue);
		}
	}
}
