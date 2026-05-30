package com.is_this_aura_clan.CanteenQ.order;

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

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;

class StaffOrderQueueControllerTest {

	@Test
	void queueReturnsActiveOrdersForStaff() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffOrderQueueService staffOrderQueueService = mock(StaffOrderQueueService.class);
		UUID stallId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		when(staffOrderQueueService.getQueue(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId)).thenReturn(
			new StaffOrderQueueResponse(
				stallId,
				List.of(
					new StaffOrderResponse(
						UUID.fromString("22222222-2222-2222-2222-222222222222"),
						UUID.fromString("33333333-3333-3333-3333-333333333333"),
						"Jane Doe",
						"jane@school.edu",
						stallId,
						new BigDecimal("45.00"),
						java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
						1,
						OrderStatus.PENDING,
						List.of()
					)
				)
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffOrderQueueController(staffOrderQueueService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/orders").param("stallId", stallId.toString()).header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.stallId").value(stallId.toString()))
			.andExpect(jsonPath("$.orders[0].studentName").value("Jane Doe"))
			.andExpect(jsonPath("$.orders[0].status").value("PENDING"));

		verify(authService).authenticate("Bearer staff-token");
	}

	@Test
	void queueSupportsDateAndStatusFilters() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffOrderQueueService staffOrderQueueService = mock(StaffOrderQueueService.class);
		UUID stallId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		LocalDate date = LocalDate.of(2026, 5, 30);
		when(staffOrderQueueService.getQueue(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			date,
			List.of(OrderStatus.COMPLETED)
		)).thenReturn(new StaffOrderQueueResponse(stallId, List.of()));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffOrderQueueController(staffOrderQueueService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/orders")
				.param("stallId", stallId.toString())
				.param("date", date.toString())
				.param("status", "COMPLETED")
				.header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.stallId").value(stallId.toString()));

		verify(authService).authenticate("Bearer staff-token");
	}
}
