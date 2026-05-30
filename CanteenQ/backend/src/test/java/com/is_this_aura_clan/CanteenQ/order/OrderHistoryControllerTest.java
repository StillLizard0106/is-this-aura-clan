package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;

class OrderHistoryControllerTest {

	@Test
	void myOrdersReturnsActiveAndHistoryLists() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		OrderHistoryService orderHistoryService = mock(OrderHistoryService.class);
		when(orderHistoryService.getMyOrders(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"))).thenReturn(
			new MyOrdersResponse(
				List.of(new OrderResponse(null, null, new BigDecimal("45.00"), java.time.LocalDateTime.of(2026, 5, 30, 8, 20), 1, OrderStatus.PENDING, List.of())),
				List.of(new OrderResponse(null, null, new BigDecimal("90.00"), java.time.LocalDateTime.of(2026, 5, 29, 8, 20), 2, OrderStatus.COMPLETED, List.of()))
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderHistoryController(orderHistoryService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/orders/my").header("Authorization", "Bearer student-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.activeOrders[0].status").value("PENDING"))
			.andExpect(jsonPath("$.history[0].status").value("COMPLETED"));

		verify(authService).authenticate("Bearer student-token");
	}
}
