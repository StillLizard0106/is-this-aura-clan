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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/assignments")
public class StaffStallAssignmentController {

	private final StaffStallAssignmentService assignmentService;

	public StaffStallAssignmentController(StaffStallAssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	@GetMapping
	public ResponseEntity<List<StaffStallAssignmentResponse>> getMyStalls(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(assignmentService.getMyStalls(principal));
	}

	@PostMapping
	public ResponseEntity<StaffStallAssignmentResponse> assignStall(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@Valid @RequestBody AssignStallRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assignStall(principal, request));
	}

	@DeleteMapping("/{stallId}")
	public ResponseEntity<Void> unassignStall(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId
	) {
		assignmentService.unassignStall(principal, stallId);
		return ResponseEntity.noContent().build();
	}
}
