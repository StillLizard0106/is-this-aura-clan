package com.is_this_aura_clan.CanteenQ.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(
	@NotNull(message = "stall id is required") UUID stallId,
	@NotNull(message = "pickup slot is required") LocalDateTime pickupSlot,
	@NotEmpty(message = "at least one item is required") List<@Valid OrderLineRequest> items
) {
	public record OrderLineRequest(
		@NotNull(message = "menu item id is required") UUID menuItemId,
		@Positive(message = "quantity must be greater than zero") int quantity
	) {
	}
}
