package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

class FirebaseAuthInterceptorTest {

	@Test
	void preflightOptionsRequestBypassesAuthentication() {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getMethod()).thenReturn("OPTIONS");

		assertTrue(interceptor.preHandle(request, response, new Object()));
		verify(authService, never()).authenticate(null);
	}

	@Test
	void accessTokenQueryParameterIsAcceptedWhenAuthorizationHeaderIsMissing() {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getMethod()).thenReturn("GET");
		when(request.getHeader("Authorization")).thenReturn(null);
		when(request.getParameter("access_token")).thenReturn("sample-token");
		when(authService.authenticate("Bearer sample-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu"), "ok")
		);

		assertTrue(interceptor.preHandle(request, response, new Object()));
		verify(authService).authenticate("Bearer sample-token");
	}
}
