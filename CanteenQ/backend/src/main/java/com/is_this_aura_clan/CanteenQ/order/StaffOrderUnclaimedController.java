package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderUnclaimedController {

	private final StaffOrderUnclaimedService staffOrderUnclaimedService;

	public StaffOrderUnclaimedController(StaffOrderUnclaimedService staffOrderUnclaimedService) {
		this.staffOrderUnclaimedService = staffOrderUnclaimedService;
	}

	@PatchMapping("/{orderId}/unclaimed")
	public ResponseEntity<StaffOrderResponse> markUnclaimed(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId
	) {
		return ResponseEntity.ok(staffOrderUnclaimedService.markUnclaimed(principal, orderId));
	}
}
