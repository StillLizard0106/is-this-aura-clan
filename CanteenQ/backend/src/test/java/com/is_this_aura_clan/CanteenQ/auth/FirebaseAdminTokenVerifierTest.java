package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

class FirebaseAdminTokenVerifierTest {

	@Test
	void verifyMapsFirebaseTokenToPrincipal() throws Exception {
		FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
		FirebaseToken firebaseToken = mock(FirebaseToken.class);
		when(firebaseAuth.verifyIdToken("sample-token")).thenReturn(firebaseToken);
		when(firebaseToken.getUid()).thenReturn("uid-123");
		when(firebaseToken.getEmail()).thenReturn("student@school.edu");

		FirebaseAdminTokenVerifier verifier = new FirebaseAdminTokenVerifier(firebaseAuth);

		FirebaseAuthenticationPrincipal principal = verifier.verify("sample-token");

		assertEquals("uid-123", principal.uid());
		assertEquals("student@school.edu", principal.email());
	}
}
