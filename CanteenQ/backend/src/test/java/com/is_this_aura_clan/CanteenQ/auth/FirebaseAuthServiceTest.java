package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FirebaseAuthServiceTest {

	@Test
	void authenticateReturnsVerifiedPrincipal() {
		FirebaseTokenVerifier verifier = token -> new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu");
		FirebaseAuthService service = new FirebaseAuthService(verifier);

		FirebaseAuthenticationResult result = service.authenticate("Bearer sample-token");

		assertTrue(result.authenticated());
		assertEquals("uid-1", result.principal().uid());
		assertEquals("student@school.edu", result.principal().email());
		assertEquals("Firebase token accepted", result.message());
	}

	@Test
	void authenticateRejectsBlankBearerToken() {
		FirebaseTokenVerifier verifier = token -> new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu");
		FirebaseAuthService service = new FirebaseAuthService(verifier);

		org.junit.jupiter.api.Assertions.assertThrows(
			InvalidFirebaseAuthorizationException.class,
			() -> service.authenticate("Bearer   ")
		);
	}
}
