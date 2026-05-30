package com.is_this_aura_clan.CanteenQ.order;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffOrderUnclaimedService {

	private final UserAuthorizationService userAuthorizationService;
	private final OrderRepository orderRepository;
	private final Clock clock;
	private final StaffOrderResponseMapper staffOrderResponseMapper;
	private final OrderNotificationService orderNotificationService;

	public StaffOrderUnclaimedService(
		UserAuthorizationService userAuthorizationService,
		OrderRepository orderRepository,
		Clock clock,
		StaffOrderResponseMapper staffOrderResponseMapper,
		OrderNotificationService orderNotificationService
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.orderRepository = orderRepository;
		this.clock = clock;
		this.staffOrderResponseMapper = staffOrderResponseMapper;
		this.orderNotificationService = orderNotificationService;
	}

	@Transactional
	public StaffOrderResponse markUnclaimed(FirebaseAuthenticationPrincipal principal, UUID orderId) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		CanteenOrder order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

		order.markUnclaimed(LocalDateTime.now(clock));
		orderRepository.save(order);
		orderNotificationService.publishStudentOrderUpdateAfterCommit(
			order.getStudent().getId(),
			new OrderStatusNotification(order.getId(), order.getStatus(), "Your order was marked unclaimed.")
		);
		return staffOrderResponseMapper.toResponse(order);
	}
}
