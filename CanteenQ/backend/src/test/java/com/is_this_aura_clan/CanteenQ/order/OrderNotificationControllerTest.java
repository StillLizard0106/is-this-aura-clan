package com.is_this_aura_clan.CanteenQ.order;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;

class OrderNotificationControllerTest {

	@Test
	void notificationsStartsSseStreamForStudent() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		when(authorizationService.requireRole(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), UserRole.STUDENT)).thenReturn(student);

		OrderNotificationService notificationService = mock(OrderNotificationService.class);
		when(notificationService.subscribeStudent(student.getId())).thenReturn(new SseEmitter());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderNotificationController(authorizationService, notificationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		MvcResult result = mockMvc.perform(get("/api/orders/notifications").header("Authorization", "Bearer student-token"))
			.andExpect(request().asyncStarted())
			.andExpect(status().isOk())
			.andReturn();

		verify(authService).authenticate("Bearer student-token");
	}

	@Test
	void notificationsAcceptsAccessTokenQueryParameter() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		when(authorizationService.requireRole(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), UserRole.STUDENT)).thenReturn(student);

		OrderNotificationService notificationService = mock(OrderNotificationService.class);
		when(notificationService.subscribeStudent(student.getId())).thenReturn(new SseEmitter());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new OrderNotificationController(authorizationService, notificationService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler())
			.build();

		mockMvc.perform(get("/api/orders/notifications").param("access_token", "student-token"))
			.andExpect(request().asyncStarted())
			.andExpect(status().isOk());

		verify(authService).authenticate("Bearer student-token");
	}

	private void assignUserId(UserAccount userAccount, UUID id) {
		try {
			java.lang.reflect.Field idField = UserAccount.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(userAccount, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}
}
