package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/stalls")
public class StaffStallController {

	private final StallManagementService stallManagementService;

	public StaffStallController(StallManagementService stallManagementService) {
		this.stallManagementService = stallManagementService;
	}

	@GetMapping
	public ResponseEntity<List<StallResponse>> list(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(stallManagementService.listStalls(principal));
	}

	@PostMapping
	public ResponseEntity<StallResponse> create(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@Valid @RequestBody StallRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(stallManagementService.createStall(principal, request));
	}

	@PutMapping("/{stallId}")
	public ResponseEntity<StallResponse> update(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId,
		@Valid @RequestBody StallRequest request
	) {
		return ResponseEntity.ok(stallManagementService.updateStall(principal, stallId, request));
	}

	@DeleteMapping("/{stallId}")
	public ResponseEntity<Void> delete(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId
	) {
		stallManagementService.deleteStall(principal, stallId);
		return ResponseEntity.noContent().build();
	}
}
