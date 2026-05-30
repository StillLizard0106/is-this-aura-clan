package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

class StaffOrderDetailControllerTest {

	@Test
	void getOrderReturnsStaffOrderResponse() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		StaffOrderDetailService service = mock(StaffOrderDetailService.class);
		UUID orderId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		UUID stallId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		when(service.getOrder(any(), any())).thenReturn(
			new StaffOrderResponse(
				orderId,
				UUID.fromString("22222222-2222-2222-2222-222222222222"),
				"Jane Doe",
				"jane@school.edu",
				stallId,
				new BigDecimal("45.00"),
				java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
				1,
				OrderStatus.PENDING,
				List.of()
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffOrderDetailController(service))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/orders/{orderId}", orderId).header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(orderId.toString()))
			.andExpect(jsonPath("$.studentName").value("Jane Doe"));

		verify(authService).authenticate("Bearer staff-token");
	}
}
