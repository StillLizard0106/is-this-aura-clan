package com.is_this_aura_clan.CanteenQ.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StaffOrderResponse(
	UUID id,
	UUID studentId,
	String studentName,
	String studentEmail,
	UUID stallId,
	BigDecimal totalPrice,
	LocalDateTime pickupSlot,
	int queueNumber,
	OrderStatus status,
	List<OrderLineResponse> items
) {
	public record OrderLineResponse(UUID menuItemId, String itemName, int quantity, BigDecimal subtotal) {
	}
}
