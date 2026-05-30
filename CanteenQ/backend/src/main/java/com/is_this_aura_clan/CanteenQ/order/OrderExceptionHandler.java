package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.is_this_aura_clan.CanteenQ.ErrorResponse;

@RestControllerAdvice
public class OrderExceptionHandler {

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("ORDER_NOT_FOUND", exception.getMessage()));
	}

	@ExceptionHandler(OrderPlacementException.class)
	public ResponseEntity<ErrorResponse> handleOrderPlacement(OrderPlacementException exception) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse("ORDER_PLACEMENT_ERROR", exception.getMessage()));
	}

	@ExceptionHandler(OrderConflictException.class)
	public ResponseEntity<ErrorResponse> handleOrderConflict(OrderConflictException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("ORDER_CONFLICT", exception.getMessage()));
	}

	@ExceptionHandler(OrderStatusTransitionException.class)
	public ResponseEntity<ErrorResponse> handleOrderStatusTransition(OrderStatusTransitionException exception) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse("INVALID_STATUS_TRANSITION", exception.getMessage()));
	}

	@ExceptionHandler(OrderActionException.class)
	public ResponseEntity<ErrorResponse> handleOrderAction(OrderActionException exception) {
		return ResponseEntity.badRequest()
			.body(new ErrorResponse("ORDER_ACTION_ERROR", exception.getMessage()));
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	public ResponseEntity<ErrorResponse> handleValidation(Exception exception) {
		String message = "Validation failed";
		if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
			message = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.orElse(message);
		} else if (exception instanceof BindException bindException) {
			message = bindException.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.orElse(message);
		}

		return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", message));
	}
}
