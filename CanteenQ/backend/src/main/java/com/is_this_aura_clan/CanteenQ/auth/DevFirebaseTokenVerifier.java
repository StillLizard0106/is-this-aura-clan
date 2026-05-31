package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "firebase.admin", name = "enabled", havingValue = "false", matchIfMissing = true)
@Profile("dev")
class DevFirebaseTokenVerifier implements FirebaseTokenVerifier {

	private static final String PREFIX = "mock-jwt-token:";

	@Override
	public FirebaseAuthenticationPrincipal verify(String token) {
		if (token == null || token.isBlank()) {
			throw new InvalidFirebaseAuthorizationException("Bearer token must not be empty");
		}

		if (!token.startsWith(PREFIX)) {
			throw new InvalidFirebaseAuthorizationException("Unsupported development token format");
		}

		String[] parts = token.split(":", 3);
		if (parts.length < 3 || parts[1].isBlank() || parts[2].isBlank()) {
			throw new InvalidFirebaseAuthorizationException("Development token must include uid and email");
		}

		return new FirebaseAuthenticationPrincipal(parts[1], parts[2]);
	}
}
