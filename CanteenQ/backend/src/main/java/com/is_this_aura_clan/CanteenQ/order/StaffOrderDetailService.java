package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffOrderDetailService {

	private final UserAuthorizationService userAuthorizationService;
	private final OrderRepository orderRepository;
	private final StaffOrderResponseMapper staffOrderResponseMapper;

	public StaffOrderDetailService(
		UserAuthorizationService userAuthorizationService,
		OrderRepository orderRepository,
		StaffOrderResponseMapper staffOrderResponseMapper
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.orderRepository = orderRepository;
		this.staffOrderResponseMapper = staffOrderResponseMapper;
	}

	@Transactional(readOnly = true)
	public StaffOrderResponse getOrder(FirebaseAuthenticationPrincipal principal, UUID orderId) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		CanteenOrder order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));
		return staffOrderResponseMapper.toResponse(order);
	}
}
