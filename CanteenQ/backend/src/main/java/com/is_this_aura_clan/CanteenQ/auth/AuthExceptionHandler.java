package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.is_this_aura_clan.CanteenQ.order.OrderConflictException;
import com.is_this_aura_clan.CanteenQ.order.OrderActionException;
import com.is_this_aura_clan.CanteenQ.order.OrderNotFoundException;
import com.is_this_aura_clan.CanteenQ.order.OrderPlacementException;
import com.is_this_aura_clan.CanteenQ.order.OrderStatusTransitionException;
import com.is_this_aura_clan.CanteenQ.ErrorResponse;

@RestControllerAdvice
public class AuthExceptionHandler {

	@ExceptionHandler(InvalidFirebaseAuthorizationException.class)
	public ResponseEntity<ErrorResponse> handleInvalidAuthorization(InvalidFirebaseAuthorizationException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_AUTHORIZATION", exception.getMessage()));
	}

	@ExceptionHandler(FirebaseAuthNotConfiguredException.class)
	public ResponseEntity<ErrorResponse> handleNotConfigured(FirebaseAuthNotConfiguredException exception) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(new ErrorResponse("FIREBASE_AUTH_NOT_CONFIGURED", exception.getMessage()));
	}

	@ExceptionHandler(InsufficientRoleException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientRole(InsufficientRoleException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ErrorResponse("FORBIDDEN_ROLE", exception.getMessage()));
	}

	@ExceptionHandler(OrderPlacementException.class)
	public ResponseEntity<ErrorResponse> handleOrderPlacement(OrderPlacementException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse("ORDER_PLACEMENT_ERROR", exception.getMessage()));
	}

	@ExceptionHandler(OrderConflictException.class)
	public ResponseEntity<ErrorResponse> handleOrderConflict(OrderConflictException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("ORDER_CONFLICT", exception.getMessage()));
	}

	@ExceptionHandler(OrderActionException.class)
	public ResponseEntity<ErrorResponse> handleOrderAction(OrderActionException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse("ORDER_ACTION_ERROR", exception.getMessage()));
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("ORDER_NOT_FOUND", exception.getMessage()));
	}

	@ExceptionHandler(OrderStatusTransitionException.class)
	public ResponseEntity<ErrorResponse> handleOrderStatusTransition(OrderStatusTransitionException exception) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse("ORDER_STATUS_TRANSITION_ERROR", exception.getMessage()));
	}
}
