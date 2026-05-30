package com.is_this_aura_clan.CanteenQ.order;

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
public class OrderHistoryService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final UserAuthorizationService userAuthorizationService;
	private final UserAccountRepository userAccountRepository;
	private final OrderRepository orderRepository;
	private final OrderResponseMapper orderResponseMapper;

	public OrderHistoryService(
		UserAuthorizationService userAuthorizationService,
		UserAccountRepository userAccountRepository,
		OrderRepository orderRepository,
		OrderResponseMapper orderResponseMapper
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.userAccountRepository = userAccountRepository;
		this.orderRepository = orderRepository;
		this.orderResponseMapper = orderResponseMapper;
	}

	@Transactional(readOnly = true)
	public MyOrdersResponse getMyOrders(FirebaseAuthenticationPrincipal principal) {
		userAuthorizationService.requireRole(principal, UserRole.STUDENT);
		UserAccount student = userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new OrderPlacementException("No student account found for the authenticated user."));

		List<OrderResponse> allOrders = orderRepository.findByStudent_IdOrderByCreatedAtDesc(student.getId())
			.stream()
			.map(orderResponseMapper::toResponse)
			.toList();

		List<OrderResponse> activeOrders = allOrders.stream()
			.filter(order -> ACTIVE_STATUSES.contains(order.status()))
			.toList();

		List<OrderResponse> history = allOrders.stream()
			.filter(order -> !ACTIVE_STATUSES.contains(order.status()))
			.toList();

		return new MyOrdersResponse(activeOrders, history);
	}
}
