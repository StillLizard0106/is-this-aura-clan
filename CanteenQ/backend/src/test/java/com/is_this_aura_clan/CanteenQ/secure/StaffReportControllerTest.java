package com.is_this_aura_clan.CanteenQ.secure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

class StaffReportControllerTest {

	@Test
	void summaryReturnsReportingDataForStaffUser() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffReportService service = mock(StaffReportService.class);
		when(service.getSummary(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), null, null)).thenReturn(
			new StaffReportResponse(
				3L,
				20L,
				8L,
				9L,
				2L,
				3L,
				4L,
				5L,
				1L,
				2L,
				List.of(new StaffStallReportResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Rice Bowl", 2L, new BigDecimal("75.00")))
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffReportController(service))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/reporting/summary").header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalStalls").value(3))
			.andExpect(jsonPath("$.activeOrders").value(9))
			.andExpect(jsonPath("$.unclaimedToday").value(2))
			.andExpect(jsonPath("$.stallBreakdowns[0].stallName").value("Rice Bowl"));

		verify(authService).authenticate("Bearer staff-token");
	}

	@Test
	void summaryRejectsStudentUser() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		StaffReportService service = mock(StaffReportService.class);
		when(service.getSummary(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), null, null)).thenThrow(
			new InsufficientRoleException("Access requires STAFF role.")
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffReportController(service))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/reporting/summary").header("Authorization", "Bearer student-token"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("FORBIDDEN_ROLE"));
	}

	@Test
	void summaryAcceptsDateRangeFilters() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffReportService service = mock(StaffReportService.class);
		LocalDate startDate = LocalDate.of(2026, 5, 28);
		LocalDate endDate = LocalDate.of(2026, 5, 29);
		when(service.getSummary(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), startDate, endDate)).thenReturn(
			new StaffReportResponse(3L, 4L, 4L, 4L, 1L, 1L, 1L, 2L, 0L, 1L, List.of())
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffReportController(service))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/reporting/summary")
				.param("startDate", startDate.toString())
				.param("endDate", endDate.toString())
				.header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalOrders").value(4))
			.andExpect(jsonPath("$.completedToday").value(2));
	}
}
