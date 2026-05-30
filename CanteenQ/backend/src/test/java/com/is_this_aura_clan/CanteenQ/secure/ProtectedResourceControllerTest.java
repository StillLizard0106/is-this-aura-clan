package com.is_this_aura_clan.CanteenQ.secure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthNotConfiguredException;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InvalidFirebaseAuthorizationException;

class ProtectedResourceControllerTest {

	@Test
	void profileReturnsProtectedUserData() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer sample-token")).thenReturn(
			new FirebaseAuthenticationResult(
				true,
				new FirebaseAuthenticationPrincipal("uid-123", "student@school.edu"),
				"Firebase token accepted"
			)
		);

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		ProtectedResourceService service = mock(ProtectedResourceService.class);
		when(service.getProtectedResource(new FirebaseAuthenticationPrincipal("uid-123", "student@school.edu"))).thenReturn(
			new ProtectedResourceResponse("Protected content accessible", "uid-123", "student@school.edu")
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProtectedResourceController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/protected/profile").header("Authorization", "Bearer sample-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Protected content accessible"))
			.andExpect(jsonPath("$.uid").value("uid-123"))
			.andExpect(jsonPath("$.email").value("student@school.edu"));

		verify(authService).authenticate("Bearer sample-token");
	}

	@Test
	void profileRejectsMissingAuthorizationHeader() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate(null)).thenThrow(new InvalidFirebaseAuthorizationException("Missing Authorization header"));

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		ProtectedResourceService service = mock(ProtectedResourceService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProtectedResourceController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/protected/profile"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION"));
	}

	@Test
	void profileReturnsUnavailableWhenFirebaseIsNotConfigured() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer sample-token")).thenThrow(
			new FirebaseAuthNotConfiguredException(
				"Firebase Admin verification is not configured yet. Provide Firebase Admin credentials before enabling token verification."
			)
		);

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		ProtectedResourceService service = mock(ProtectedResourceService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ProtectedResourceController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/protected/profile").header("Authorization", "Bearer sample-token"))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.code").value("FIREBASE_AUTH_NOT_CONFIGURED"));
	}
}
