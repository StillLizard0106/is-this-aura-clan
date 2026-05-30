package com.is_this_aura_clan.CanteenQ.catalog;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuItemRequest(
	@NotBlank(message = "item name is required") String itemName,
	@NotBlank(message = "description is required") String description,
	@NotNull(message = "price is required") @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than zero") BigDecimal price,
	@NotBlank(message = "category is required") String category,
	boolean available
) {
}
