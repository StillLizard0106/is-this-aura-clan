package com.is_this_aura_clan.CanteenQ.secure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff")
public class StaffDashboardController {

	private final StaffDashboardService staffDashboardService;

	public StaffDashboardController(StaffDashboardService staffDashboardService) {
		this.staffDashboardService = staffDashboardService;
	}

	@GetMapping("/dashboard")
	public ResponseEntity<StaffDashboardResponse> dashboard(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(staffDashboardService.getDashboard(principal));
	}
}
