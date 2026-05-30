package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/orders")
public class OrderNotificationController {

	private final UserAuthorizationService userAuthorizationService;
	private final OrderNotificationService orderNotificationService;

	public OrderNotificationController(
		UserAuthorizationService userAuthorizationService,
		OrderNotificationService orderNotificationService
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.orderNotificationService = orderNotificationService;
	}

	@GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter notifications(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		UserAccount student = userAuthorizationService.requireRole(principal, UserRole.STUDENT);
		return orderNotificationService.subscribeStudent(student.getId());
	}
}
