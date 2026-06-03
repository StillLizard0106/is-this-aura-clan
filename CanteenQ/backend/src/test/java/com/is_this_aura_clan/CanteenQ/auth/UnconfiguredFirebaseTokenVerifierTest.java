package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UnconfiguredFirebaseTokenVerifierTest {

	@Test
	void verifyAcceptsMockDevelopmentTokenWhenFirebaseAdminIsNotConfigured() {
		UnconfiguredFirebaseTokenVerifier verifier = new UnconfiguredFirebaseTokenVerifier();

		FirebaseAuthenticationPrincipal principal = verifier.verify("mock-jwt-token:uid-1:student@school.edu");

		assertEquals(new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu"), principal);
	}

	@Test
	void verifyThrowsWhenFirebaseAdminIsNotConfiguredForRealToken() {
		UnconfiguredFirebaseTokenVerifier verifier = new UnconfiguredFirebaseTokenVerifier();

		assertThrows(FirebaseAuthNotConfiguredException.class, () -> verifier.verify("sample-token"));
	}
}
