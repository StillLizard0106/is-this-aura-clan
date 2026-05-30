package com.is_this_aura_clan.CanteenQ.auth;

public record FirebaseAuthenticationResult(boolean authenticated, FirebaseAuthenticationPrincipal principal, String message) {
}
