package com.is_this_aura_clan.CanteenQ.auth;

import java.util.Objects;

final class BearerTokenExtractor {

	private static final String BEARER_PREFIX = "Bearer ";

	private BearerTokenExtractor() {
	}

	static String extract(String authorizationHeader) {
		String header = authorizationHeader == null ? "" : authorizationHeader.trim();
		if (header.isEmpty()) {
			throw new InvalidFirebaseAuthorizationException("Missing Authorization header");
		}
		if (!header.startsWith(BEARER_PREFIX)) {
			throw new InvalidFirebaseAuthorizationException("Authorization header must use Bearer scheme");
		}

		String token = header.substring(BEARER_PREFIX.length()).trim();
		if (token.isEmpty()) {
			throw new InvalidFirebaseAuthorizationException("Bearer token must not be empty");
		}
		return token;
	}
}
