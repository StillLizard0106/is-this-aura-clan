package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderStatusController {

	private final StaffOrderStatusService staffOrderStatusService;

	public StaffOrderStatusController(StaffOrderStatusService staffOrderStatusService) {
		this.staffOrderStatusService = staffOrderStatusService;
	}

	@PatchMapping("/{orderId}")
	public ResponseEntity<StaffOrderResponse> updateStatus(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId,
		@Valid @RequestBody StaffOrderStatusUpdateRequest request
	) {
		return ResponseEntity.ok(staffOrderStatusService.updateStatus(principal, orderId, request));
	}
}
