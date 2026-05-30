package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class OrderDetailService {

	private final UserAuthorizationService userAuthorizationService;
	private final UserAccountRepository userAccountRepository;
	private final OrderRepository orderRepository;
	private final OrderResponseMapper orderResponseMapper;

	public OrderDetailService(
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
	public OrderResponse getOrder(FirebaseAuthenticationPrincipal principal, UUID orderId) {
		userAuthorizationService.requireRole(principal, UserRole.STUDENT);
		UserAccount student = userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new OrderPlacementException("No student account found for the authenticated user."));

		CanteenOrder order = orderRepository.findByIdAndStudent_Id(orderId, student.getId())
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

		return orderResponseMapper.toResponse(order);
	}
}
