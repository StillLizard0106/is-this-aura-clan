package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.UUID;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;
	private final OrderDetailService orderDetailService;
	private final OrderCancellationService orderCancellationService;

	public OrderController(
		OrderService orderService,
		OrderDetailService orderDetailService,
		OrderCancellationService orderCancellationService
	) {
		this.orderService = orderService;
		this.orderDetailService = orderDetailService;
		this.orderCancellationService = orderCancellationService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> placeOrder(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@Valid @RequestBody OrderRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(principal, request));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponse> getOrder(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId
	) {
		return ResponseEntity.ok(orderDetailService.getOrder(principal, orderId));
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<OrderResponse> cancelOrder(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@PathVariable UUID orderId
	) {
		return ResponseEntity.ok(orderCancellationService.cancel(principal, orderId));
	}
}
