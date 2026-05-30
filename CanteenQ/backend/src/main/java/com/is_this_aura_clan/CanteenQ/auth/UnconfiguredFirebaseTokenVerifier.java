package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "firebase.admin", name = "enabled", havingValue = "false", matchIfMissing = true)
class UnconfiguredFirebaseTokenVerifier implements FirebaseTokenVerifier {

	@Override
	public FirebaseAuthenticationPrincipal verify(String token) {
		throw new FirebaseAuthNotConfiguredException(
			"Firebase Admin verification is not configured yet. Provide Firebase Admin credentials before enabling token verification."
		);
	}
}
