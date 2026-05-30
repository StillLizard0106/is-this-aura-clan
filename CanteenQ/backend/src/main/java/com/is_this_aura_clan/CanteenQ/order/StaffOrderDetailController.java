package com.is_this_aura_clan.CanteenQ.order;

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
@RequestMapping("/api/staff/orders")
public class StaffOrderDetailController {

	private final StaffOrderDetailService staffOrderDetailService;

	public StaffOrderDetailController(StaffOrderDetailService staffOrderDetailService) {
		this.staffOrderDetailService = staffOrderDetailService;
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<StaffOrderResponse> getOrder(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId
	) {
		return ResponseEntity.ok(staffOrderDetailService.getOrder(principal, orderId));
	}
}
