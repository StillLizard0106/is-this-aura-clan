package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/orders/{orderId}/audit")
public class StaffOrderAuditController {

	private final StaffOrderAuditService staffOrderAuditService;

	public StaffOrderAuditController(StaffOrderAuditService staffOrderAuditService) {
		this.staffOrderAuditService = staffOrderAuditService;
	}

	@GetMapping
	public ResponseEntity<List<OrderAuditTrailResponse>> getOrderAuditTrail(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId
	) {
		List<OrderAuditTrailResponse> auditTrail = staffOrderAuditService.getOrderAuditTrail(principal, orderId);
		return ResponseEntity.ok(auditTrail);
	}
}
