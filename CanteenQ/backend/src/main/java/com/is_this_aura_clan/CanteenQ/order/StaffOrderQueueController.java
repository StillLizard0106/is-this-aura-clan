package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/orders")
public class StaffOrderQueueController {

	private final StaffOrderQueueService staffOrderQueueService;

	public StaffOrderQueueController(StaffOrderQueueService staffOrderQueueService) {
		this.staffOrderQueueService = staffOrderQueueService;
	}

	@GetMapping
	public ResponseEntity<StaffOrderQueueResponse> queue(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@RequestParam UUID stallId,
		@RequestParam(required = false) LocalDate date,
		@RequestParam(required = false) List<OrderStatus> status
	) {
		if (date == null && (status == null || status.isEmpty())) {
			return ResponseEntity.ok(staffOrderQueueService.getQueue(principal, stallId));
		}

		return ResponseEntity.ok(staffOrderQueueService.getQueue(principal, stallId, date, status));
	}
}
