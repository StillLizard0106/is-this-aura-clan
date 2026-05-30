package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UnconfiguredFirebaseTokenVerifierTest {

	@Test
	void verifyThrowsWhenFirebaseAdminIsNotConfigured() {
		UnconfiguredFirebaseTokenVerifier verifier = new UnconfiguredFirebaseTokenVerifier();

		assertThrows(FirebaseAuthNotConfiguredException.class, () -> verifier.verify("sample-token"));
	}
}
