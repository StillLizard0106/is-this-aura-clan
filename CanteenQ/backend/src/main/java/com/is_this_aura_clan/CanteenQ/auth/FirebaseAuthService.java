package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

	private final FirebaseTokenVerifier tokenVerifier;

	public FirebaseAuthService(FirebaseTokenVerifier tokenVerifier) {
		this.tokenVerifier = tokenVerifier;
	}

	public FirebaseAuthenticationResult authenticate(String authorizationHeader) {
		String token = BearerTokenExtractor.extract(authorizationHeader);
		FirebaseAuthenticationPrincipal principal = tokenVerifier.verify(token);
		return new FirebaseAuthenticationResult(true, principal, "Firebase token accepted");
	}
}
