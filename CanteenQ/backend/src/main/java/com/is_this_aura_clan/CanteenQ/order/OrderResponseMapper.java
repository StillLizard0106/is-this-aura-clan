package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.stereotype.Component;

@Component
class OrderResponseMapper {

	OrderResponse toResponse(CanteenOrder order) {
		return new OrderResponse(
			order.getId(),
			order.getStallId(),
			order.getTotalPrice(),
			order.getPickupSlot(),
			order.getQueueNumber(),
			order.getStatus(),
			order.getItems().stream()
				.map(item -> new OrderResponse.OrderLineResponse(
					item.getMenuItem().getId(),
					item.getMenuItem().getItemName(),
					item.getQuantity(),
					item.getSubtotal()
				))
				.toList()
		);
	}
}
