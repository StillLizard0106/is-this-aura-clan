package com.is_this_aura_clan.CanteenQ.secure;

import org.springframework.stereotype.Service;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class StaffDashboardService {

	private final UserAuthorizationService userAuthorizationService;

	public StaffDashboardService(UserAuthorizationService userAuthorizationService) {
		this.userAuthorizationService = userAuthorizationService;
	}

	public StaffDashboardResponse getDashboard(FirebaseAuthenticationPrincipal principal) {
		UserAccount userAccount = userAuthorizationService.requireRole(principal, UserRole.STAFF);
		return new StaffDashboardResponse(
			"Staff dashboard access granted",
			userAccount.getName(),
			userAccount.getEmail(),
			userAccount.getRole()
		);
	}
}
