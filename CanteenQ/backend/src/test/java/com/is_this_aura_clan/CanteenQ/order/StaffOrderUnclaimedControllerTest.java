package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;

class StaffOrderUnclaimedControllerTest {

	@Test
	void markUnclaimedReturnsUpdatedOrder() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffOrderUnclaimedService service = mock(StaffOrderUnclaimedService.class);
		UUID orderId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		UUID stallId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		when(service.markUnclaimed(any(), any())).thenReturn(
			new StaffOrderResponse(
				orderId,
				UUID.fromString("22222222-2222-2222-2222-222222222222"),
				"Jane Doe",
				"jane@school.edu",
				stallId,
				new BigDecimal("45.00"),
				java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
				1,
				OrderStatus.UNCLAIMED,
				List.of()
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffOrderUnclaimedController(service))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(patch("/api/staff/orders/{orderId}/unclaimed", orderId).header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("UNCLAIMED"));

		verify(authService).authenticate("Bearer staff-token");
	}
}
