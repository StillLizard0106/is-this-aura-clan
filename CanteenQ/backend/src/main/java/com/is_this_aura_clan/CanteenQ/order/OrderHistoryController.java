package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/orders")
public class OrderHistoryController {

	private final OrderHistoryService orderHistoryService;

	public OrderHistoryController(OrderHistoryService orderHistoryService) {
		this.orderHistoryService = orderHistoryService;
	}

	@GetMapping("/my")
	public ResponseEntity<MyOrdersResponse> myOrders(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(orderHistoryService.getMyOrders(principal));
	}
}
