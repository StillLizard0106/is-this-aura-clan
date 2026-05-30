package com.is_this_aura_clan.CanteenQ.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.account.UserAccountSyncService;

class AuthControllerTest {

	@Test
	void verifyReturnsPrincipalForValidToken() throws Exception {
		FirebaseAuthService service = mock(FirebaseAuthService.class);
		UserAccountSyncService syncService = mock(UserAccountSyncService.class);
		when(service.authenticate("Bearer sample-token")).thenReturn(
			new FirebaseAuthenticationResult(
				true,
				new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu"),
				"Firebase token accepted"
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service, syncService))
			.setControllerAdvice(new AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/auth/verify").header("Authorization", "Bearer sample-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.authenticated").value(true))
			.andExpect(jsonPath("$.principal.uid").value("uid-1"))
			.andExpect(jsonPath("$.principal.email").value("student@school.edu"))
			.andExpect(jsonPath("$.message").value("Firebase token accepted"));

		verify(syncService).sync(new FirebaseAuthenticationPrincipal("uid-1", "student@school.edu"));
	}

	@Test
	void verifyRejectsMissingAuthorizationHeader() throws Exception {
		FirebaseAuthService service = mock(FirebaseAuthService.class);
		UserAccountSyncService syncService = mock(UserAccountSyncService.class);
		when(service.authenticate(null)).thenThrow(new InvalidFirebaseAuthorizationException("Missing Authorization header"));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service, syncService))
			.setControllerAdvice(new AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/auth/verify"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION"))
			.andExpect(jsonPath("$.message").value("Missing Authorization header"));
	}

	@Test
	void verifyReturnsUnavailableWhenFirebaseIsNotConfigured() throws Exception {
		FirebaseAuthService service = mock(FirebaseAuthService.class);
		UserAccountSyncService syncService = mock(UserAccountSyncService.class);
		when(service.authenticate("Bearer sample-token")).thenThrow(
			new FirebaseAuthNotConfiguredException(
				"Firebase Admin verification is not configured yet. Provide Firebase Admin credentials before enabling token verification."
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service, syncService))
			.setControllerAdvice(new AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/auth/verify").header("Authorization", "Bearer sample-token"))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.code").value("FIREBASE_AUTH_NOT_CONFIGURED"));
	}
}
