package com.is_this_aura_clan.CanteenQ.auth;

import com.is_this_aura_clan.CanteenQ.account.UserRole;

public record FirebaseAuthenticationResult(
	boolean authenticated,
	FirebaseAuthenticationPrincipal principal,
	String message,
	UserRole role
) {

	public FirebaseAuthenticationResult(boolean authenticated, FirebaseAuthenticationPrincipal principal, String message) {
		this(authenticated, principal, message, null);
	}
}
