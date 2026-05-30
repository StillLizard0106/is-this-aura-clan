package com.is_this_aura_clan.CanteenQ.order;

import jakarta.validation.constraints.NotNull;

public record StaffOrderStatusUpdateRequest(
	@NotNull(message = "status is required") OrderStatus status
) {
}
