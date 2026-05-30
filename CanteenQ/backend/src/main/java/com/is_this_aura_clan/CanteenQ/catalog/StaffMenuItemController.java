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
@RequestMapping("/api/staff/stalls/{stallId}/menu-items")
public class StaffMenuItemController {

	private final MenuItemManagementService menuItemManagementService;

	public StaffMenuItemController(MenuItemManagementService menuItemManagementService) {
		this.menuItemManagementService = menuItemManagementService;
	}

	@GetMapping
	public ResponseEntity<List<MenuItemResponse>> list(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId
	) {
		return ResponseEntity.ok(menuItemManagementService.listMenuItems(principal, stallId));
	}

	@PostMapping
	public ResponseEntity<MenuItemResponse> create(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId,
		@Valid @RequestBody MenuItemRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(menuItemManagementService.createMenuItem(principal, stallId, request));
	}

	@PutMapping("/{menuItemId}")
	public ResponseEntity<MenuItemResponse> update(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId,
		@PathVariable UUID menuItemId,
		@Valid @RequestBody MenuItemRequest request
	) {
		return ResponseEntity.ok(menuItemManagementService.updateMenuItem(principal, stallId, menuItemId, request));
	}

	@DeleteMapping("/{menuItemId}")
	public ResponseEntity<Void> delete(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID stallId,
		@PathVariable UUID menuItemId
	) {
		menuItemManagementService.deleteMenuItem(principal, stallId, menuItemId);
		return ResponseEntity.noContent().build();
	}
}
