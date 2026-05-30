package com.is_this_aura_clan.CanteenQ.catalog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

class StaffStallControllerTest {

	@Test
	void listReturnsStallsForStaff() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StallManagementService stallManagementService = mock(StallManagementService.class);
		when(stallManagementService.listStalls(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"))).thenReturn(
			List.of(new StallResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM"))
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffStallController(stallManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/stalls").header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].stallName").value("Rice Bowl"));

		verify(authService).authenticate("Bearer staff-token");
	}

	@Test
	void createReturnsCreatedStall() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StallManagementService stallManagementService = mock(StallManagementService.class);
		when(stallManagementService.createStall(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			new StallRequest("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM")
		)).thenReturn(new StallResponse(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM"));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffStallController(stallManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(post("/api/staff/stalls")
				.header("Authorization", "Bearer staff-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"stallName\":\"Rice Bowl\",\"vendorName\":\"A. Vendor\",\"operatingHours\":\"8:00 AM - 2:00 PM\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.stallName").value("Rice Bowl"));
	}

	@Test
	void updateReturnsUpdatedStall() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		UUID stallId = UUID.fromString("33333333-3333-3333-3333-333333333333");
		StallManagementService stallManagementService = mock(StallManagementService.class);
		when(stallManagementService.updateStall(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			new StallRequest("Snack Corner", "B. Vendor", "9:00 AM - 3:00 PM")
		)).thenReturn(new StallResponse(stallId, "Snack Corner", "B. Vendor", "9:00 AM - 3:00 PM"));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffStallController(stallManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(put("/api/staff/stalls/{stallId}", stallId)
				.header("Authorization", "Bearer staff-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"stallName\":\"Snack Corner\",\"vendorName\":\"B. Vendor\",\"operatingHours\":\"9:00 AM - 3:00 PM\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.stallName").value("Snack Corner"));
	}

	@Test
	void deleteReturnsNoContent() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		UUID stallId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		StallManagementService stallManagementService = mock(StallManagementService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffStallController(stallManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(delete("/api/staff/stalls/{stallId}", stallId)
				.header("Authorization", "Bearer staff-token"))
			.andExpect(status().isNoContent());

		verify(stallManagementService).deleteStall(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId);
	}

	@Test
	void createReturnsForbiddenForStudentAccount() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		StallManagementService stallManagementService = mock(StallManagementService.class);
		when(stallManagementService.createStall(
			new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"),
			new StallRequest("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM")
		)).thenThrow(new InsufficientRoleException("Access requires STAFF role."));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffStallController(stallManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(post("/api/staff/stalls")
				.header("Authorization", "Bearer student-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"stallName\":\"Rice Bowl\",\"vendorName\":\"A. Vendor\",\"operatingHours\":\"8:00 AM - 2:00 PM\"}"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("FORBIDDEN_ROLE"));
	}
}
