package com.is_this_aura_clan.CanteenQ.order;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderUnclaimedScheduler {

	private final OrderRepository orderRepository;
	private final Clock clock;
	private final OrderNotificationService orderNotificationService;
	private final OrderAuditTrailService auditTrailService;

	public OrderUnclaimedScheduler(
		OrderRepository orderRepository,
		Clock clock,
		OrderNotificationService orderNotificationService,
		OrderAuditTrailService auditTrailService
	) {
		this.orderRepository = orderRepository;
		this.clock = clock;
		this.orderNotificationService = orderNotificationService;
		this.auditTrailService = auditTrailService;
	}

	@Scheduled(fixedDelayString = "${app.orders.unclaimed-check-delay-ms:60000}")
	@Transactional
	public void markExpiredOrdersAsUnclaimed() {
		LocalDateTime now = LocalDateTime.now(clock);
		// Orders whose pickup slot was more than 15 minutes ago are past the grace period.
    	// This mirrors the check in CanteenOrder.markUnclaimed(), keeping both consistent.
		LocalDateTime cutoff = now.minusMinutes(15);
		List<CanteenOrder> expiredOrders = orderRepository.findByStatusAndPickupSlotLessThanEqualOrderByPickupSlotAscQueueNumberAsc(OrderStatus.READY, cutoff);
		if (expiredOrders.isEmpty()) {
			return;
		}

		expiredOrders.forEach(order -> {
			OrderStatus previousStatus = order.getStatus();
			order.markUnclaimed(now);
			auditTrailService.recordStatusChange(
				order.getId(),
				previousStatus,
				OrderStatus.UNCLAIMED,
				null
			);
		});
		// Save all updates in a single batch before sending any notifications.
		// This keeps the transaction clean: notifications go out after commit
		// via publishStudentOrderUpdateAfterCommit(), so they fire only if
		// the database write actually succeeded.
		orderRepository.saveAll(expiredOrders);
		expiredOrders.forEach(order ->
			orderNotificationService.publishStudentOrderUpdateAfterCommit(
				order.getStudent().getId(),
				new OrderStatusNotification(order.getId(), order.getStatus(), "Your order was marked unclaimed.")
			)
		);
	}
}
