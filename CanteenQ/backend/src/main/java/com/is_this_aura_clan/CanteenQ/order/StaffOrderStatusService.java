package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffOrderStatusService {

	private final UserAuthorizationService userAuthorizationService;
	private final OrderRepository orderRepository;
	private final StaffOrderResponseMapper staffOrderResponseMapper;
	private final OrderNotificationService orderNotificationService;
	private final OrderAuditTrailService auditTrailService;

	public StaffOrderStatusService(
		UserAuthorizationService userAuthorizationService,
		OrderRepository orderRepository,
		StaffOrderResponseMapper staffOrderResponseMapper,
		OrderNotificationService orderNotificationService,
		OrderAuditTrailService auditTrailService
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.orderRepository = orderRepository;
		this.staffOrderResponseMapper = staffOrderResponseMapper;
		this.orderNotificationService = orderNotificationService;
		this.auditTrailService = auditTrailService;
	}

	@Transactional
	public StaffOrderResponse updateStatus(
		FirebaseAuthenticationPrincipal principal,
		UUID orderId,
		StaffOrderStatusUpdateRequest request
	) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		CanteenOrder order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

		OrderStatus previousStatus = order.getStatus();
		order.updateStatus(request.status());
		orderRepository.save(order);
		auditTrailService.recordStatusChange(
			orderId,
			previousStatus,
			request.status(),
			principal.email()
		);
		orderNotificationService.publishStudentOrderUpdateAfterCommit(
			order.getStudent().getId(),
			new OrderStatusNotification(
				order.getId(),
				order.getStatus(),
				request.status() == OrderStatus.CANCELLED
					? "Your order was rejected by staff."
					: "Your order status changed to " + order.getStatus() + "."
			)
		);
		return staffOrderResponseMapper.toResponse(order);
	}
}
