package com.is_this_aura_clan.CanteenQ.catalog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.is_this_aura_clan.CanteenQ.ErrorResponse;

@RestControllerAdvice
public class CatalogExceptionHandler {

	@ExceptionHandler(StallNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleStallNotFound(StallNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("STALL_NOT_FOUND", exception.getMessage()));
	}

	@ExceptionHandler(DuplicateStallException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateStall(DuplicateStallException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("STALL_ALREADY_EXISTS", exception.getMessage()));
	}

	@ExceptionHandler(DuplicateMenuItemException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateMenuItem(DuplicateMenuItemException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("MENU_ITEM_ALREADY_EXISTS", exception.getMessage()));
	}

	@ExceptionHandler(MenuItemNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleMenuItemNotFound(MenuItemNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse("MENU_ITEM_NOT_FOUND", exception.getMessage()));
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
