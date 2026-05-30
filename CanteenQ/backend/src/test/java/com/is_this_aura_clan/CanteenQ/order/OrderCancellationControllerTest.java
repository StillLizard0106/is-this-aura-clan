package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

class OrderCancellationControllerTest {

	@Test
	void cancelReturnsUpdatedOrder() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		OrderService orderService = mock(OrderService.class);
		OrderCancellationService cancellationService = mock(OrderCancellationService.class);
		UUID orderId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		when(cancellationService.cancel(any(), any())).thenReturn(
			new OrderResponse(
				orderId,
				UUID.fromString("11111111-1111-1111-1111-111111111111"),
				new BigDecimal("45.00"),
				java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
				1,
				OrderStatus.CANCELLED,
				List.of()
			)
		);

		OrderDetailService orderDetailService = mock(OrderDetailService.class);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, orderDetailService, cancellationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(delete("/api/orders/{orderId}", orderId).header("Authorization", "Bearer student-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("CANCELLED"));

		verify(authService).authenticate("Bearer student-token");
	}
}
