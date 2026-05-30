package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffOrderAuditService {

	private final UserAuthorizationService userAuthorizationService;
	private final OrderRepository orderRepository;
	private final OrderAuditTrailService auditTrailService;
	private final OrderAuditTrailResponseMapper auditTrailResponseMapper;

	public StaffOrderAuditService(
		UserAuthorizationService userAuthorizationService,
		OrderRepository orderRepository,
		OrderAuditTrailService auditTrailService,
		OrderAuditTrailResponseMapper auditTrailResponseMapper
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.orderRepository = orderRepository;
		this.auditTrailService = auditTrailService;
		this.auditTrailResponseMapper = auditTrailResponseMapper;
	}

	public List<OrderAuditTrailResponse> getOrderAuditTrail(FirebaseAuthenticationPrincipal principal, UUID orderId) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
		orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderNotFoundException("No order found for id " + orderId));

		List<OrderAuditTrail> auditTrail = auditTrailService.getAuditTrail(orderId);
		return auditTrailResponseMapper.toResponseList(auditTrail);
	}
}
