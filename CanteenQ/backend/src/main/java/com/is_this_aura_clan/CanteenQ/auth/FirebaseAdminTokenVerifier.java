package com.is_this_aura_clan.CanteenQ.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

class FirebaseAdminTokenVerifier implements FirebaseTokenVerifier {

	private final FirebaseAuth firebaseAuth;

	FirebaseAdminTokenVerifier(FirebaseAuth firebaseAuth) {
		this.firebaseAuth = firebaseAuth;
	}

	@Override
	public FirebaseAuthenticationPrincipal verify(String token) {
		try {
			FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
			return new FirebaseAuthenticationPrincipal(firebaseToken.getUid(), firebaseToken.getEmail());
		} catch (Exception exception) {
			throw new InvalidFirebaseAuthorizationException("Firebase token verification failed: " + exception.getMessage());
		}
	}
}
