package com.is_this_aura_clan.CanteenQ.secure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

class ProtectedResourceServiceTest {

	@Test
	void getProtectedResourceReturnsAuthenticatedUserDetails() {
		ProtectedResourceService service = new ProtectedResourceService();

		ProtectedResourceResponse response = service.getProtectedResource(
			new FirebaseAuthenticationPrincipal("uid-123", "student@school.edu")
		);

		assertEquals("Protected content accessible", response.message());
		assertEquals("uid-123", response.uid());
		assertEquals("student@school.edu", response.email());
	}
}
