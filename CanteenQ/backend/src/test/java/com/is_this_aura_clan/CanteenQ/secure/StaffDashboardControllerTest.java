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

import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthNotConfiguredException;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InvalidFirebaseAuthorizationException;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

class StaffDashboardControllerTest {

	@Test
	void dashboardReturnsStaffDataForStaffUser() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(
				true,
				new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
				"Firebase token accepted"
			)
		);

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		StaffDashboardService service = mock(StaffDashboardService.class);
		when(service.getDashboard(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"))).thenReturn(
			new StaffDashboardResponse("Staff dashboard access granted", "Staff User", "staff@school.edu", UserRole.STAFF)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffDashboardController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/dashboard").header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("Staff dashboard access granted"))
			.andExpect(jsonPath("$.name").value("Staff User"))
			.andExpect(jsonPath("$.email").value("staff@school.edu"))
			.andExpect(jsonPath("$.role").value("STAFF"));

		verify(authService).authenticate("Bearer staff-token");
	}

	@Test
	void dashboardRejectsStudentUser() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(
				true,
				new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"),
				"Firebase token accepted"
			)
		);

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		StaffDashboardService service = mock(StaffDashboardService.class);
		when(service.getDashboard(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"))).thenThrow(
			new InsufficientRoleException("Access requires STAFF role.")
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffDashboardController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/dashboard").header("Authorization", "Bearer student-token"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("FORBIDDEN_ROLE"));
	}

	@Test
	void dashboardStillUsesAuthErrorMappingForMissingHeader() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate(null)).thenThrow(new InvalidFirebaseAuthorizationException("Missing Authorization header"));

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		StaffDashboardService service = mock(StaffDashboardService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffDashboardController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/dashboard"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION"));
	}

	@Test
	void dashboardStillUsesAuthErrorMappingForFirebaseNotConfigured() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenThrow(
			new FirebaseAuthNotConfiguredException(
				"Firebase Admin verification is not configured yet. Provide Firebase Admin credentials before enabling token verification."
			)
		);

		FirebaseAuthInterceptor interceptor = new FirebaseAuthInterceptor(authService);
		StaffDashboardService service = mock(StaffDashboardService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffDashboardController(service))
			.addInterceptors(interceptor)
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/dashboard").header("Authorization", "Bearer staff-token"))
			.andExpect(status().isServiceUnavailable())
			.andExpect(jsonPath("$.code").value("FIREBASE_AUTH_NOT_CONFIGURED"));
	}
}
