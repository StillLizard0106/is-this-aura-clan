package com.is_this_aura_clan.CanteenQ.order;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class OrderCancellationService {

	private final UserAuthorizationService userAuthorizationService;
	private final UserAccountRepository userAccountRepository;
	private final OrderRepository orderRepository;
	private final Clock clock;
	private final OrderResponseMapper orderResponseMapper;
	private final OrderNotificationService orderNotificationService;
	private final OrderAuditTrailService auditTrailService;

	public OrderCancellationService(
		UserAuthorizationService userAuthorizationService,
		UserAccountRepository userAccountRepository,
		OrderRepository orderRepository,
		Clock clock,
		OrderResponseMapper orderResponseMapper,
		OrderNotificationService orderNotificationService,
		OrderAuditTrailService auditTrailService
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.userAccountRepository = userAccountRepository;
		this.orderRepository = orderRepository;
		this.clock = clock;
		this.orderResponseMapper = orderResponseMapper;
		this.orderNotificationService = orderNotificationService;
		this.auditTrailService = auditTrailService;
	}

	@Transactional
	public OrderResponse cancel(FirebaseAuthenticationPrincipal principal, UUID orderId) {
		userAuthorizationService.requireRole(principal, UserRole.STUDENT);
		UserAccount student = userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new OrderActionException("No student account found for the authenticated user."));

		CanteenOrder order = orderRepository.findByIdAndStudent_Id(orderId, student.getId())
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

		OrderStatus previousStatus = order.getStatus();
		order.cancel(LocalDateTime.now(clock));
		orderRepository.save(order);
		auditTrailService.recordStatusChange(
			orderId,
			previousStatus,
			OrderStatus.CANCELLED,
			principal.email()
		);
		orderNotificationService.publishStudentOrderUpdateAfterCommit(
			student.getId(),
			new OrderStatusNotification(order.getId(), order.getStatus(), "Your order was cancelled.")
		);
		return orderResponseMapper.toResponse(order);
	}
}
