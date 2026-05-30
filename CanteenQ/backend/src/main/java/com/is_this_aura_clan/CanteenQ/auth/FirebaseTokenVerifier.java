package com.is_this_aura_clan.CanteenQ.auth;

public interface FirebaseTokenVerifier {

	FirebaseAuthenticationPrincipal verify(String token);
}
