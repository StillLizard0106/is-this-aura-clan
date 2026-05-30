package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItemRepository;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;

class OrderControllerTest {

	@Test
	void placeOrderReturnsCreatedResponse() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		UserAuthorizationService userAuthorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		Clock clock = Clock.fixed(Instant.parse("2026-05-30T08:00:00Z"), ZoneOffset.UTC);
		OrderService orderService = mock(OrderService.class);
		OrderDetailService orderDetailService = mock(OrderDetailService.class);
		OrderCancellationService cancellationService = mock(OrderCancellationService.class);
		UUID stallId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		UUID menuItemId = UUID.fromString("22222222-2222-2222-2222-222222222222");
		when(orderService.placeOrder(any(), any())).thenReturn(
			new OrderResponse(
				UUID.fromString("33333333-3333-3333-3333-333333333333"),
				stallId,
				new BigDecimal("90.00"),
				java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
				1,
				OrderStatus.PENDING,
				List.of(new OrderResponse.OrderLineResponse(menuItemId, "Chicken Rice", 2, new BigDecimal("90.00")))
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, orderDetailService, cancellationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(post("/api/orders")
				.header("Authorization", "Bearer student-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"stallId\":\"" + stallId + "\",\"pickupSlot\":\"2026-05-30T08:20:00\",\"items\":[{\"menuItemId\":\"" + menuItemId + "\",\"quantity\":2}]}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.totalPrice").value(90.00))
			.andExpect(jsonPath("$.status").value("PENDING"));
	}

	@Test
	void placeOrderReturnsForbiddenForStaffAuthorizationMismatch() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		OrderService orderService = mock(OrderService.class);
		when(orderService.placeOrder(any(), any())).thenThrow(new InsufficientRoleException("Access requires STUDENT role."));
		OrderDetailService orderDetailService = mock(OrderDetailService.class);
		OrderCancellationService cancellationService = mock(OrderCancellationService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, orderDetailService, cancellationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(post("/api/orders")
				.header("Authorization", "Bearer staff-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"stallId\":\"11111111-1111-1111-1111-111111111111\",\"pickupSlot\":\"2026-05-30T08:20:00\",\"items\":[{\"menuItemId\":\"22222222-2222-2222-2222-222222222222\",\"quantity\":2}]}"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("FORBIDDEN_ROLE"));
	}

	@Test
	void cancelOrderReturnsUpdatedResponse() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		OrderService orderService = mock(OrderService.class);
		OrderDetailService orderDetailService = mock(OrderDetailService.class);
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

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, orderDetailService, cancellationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(delete("/api/orders/{orderId}", orderId).header("Authorization", "Bearer student-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("CANCELLED"));
	}

	@Test
	void getOrderReturnsDetailResponse() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		OrderService orderService = mock(OrderService.class);
		OrderDetailService orderDetailService = mock(OrderDetailService.class);
		OrderCancellationService cancellationService = mock(OrderCancellationService.class);
		UUID orderId = UUID.fromString("55555555-5555-5555-5555-555555555555");
		when(orderDetailService.getOrder(any(), any())).thenReturn(
			new OrderResponse(
				orderId,
				UUID.fromString("11111111-1111-1111-1111-111111111111"),
				new BigDecimal("45.00"),
				java.time.LocalDateTime.of(2026, 5, 30, 8, 20),
				1,
				OrderStatus.PENDING,
				List.of()
			)
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService, orderDetailService, cancellationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/orders/{orderId}", orderId).header("Authorization", "Bearer student-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("PENDING"))
			.andExpect(jsonPath("$.id").value(orderId.toString()));
	}
}
