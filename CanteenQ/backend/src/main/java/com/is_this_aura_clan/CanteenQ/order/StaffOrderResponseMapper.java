package com.is_this_aura_clan.CanteenQ.order;

import org.springframework.stereotype.Component;

@Component
class StaffOrderResponseMapper {

	StaffOrderResponse toResponse(CanteenOrder order) {
		return new StaffOrderResponse(
			order.getId(),
			order.getStudent().getId(),
			order.getStudent().getName(),
			order.getStudent().getEmail(),
			order.getStallId(),
			order.getTotalPrice(),
			order.getPickupSlot(),
			order.getQueueNumber(),
			order.getStatus(),
			order.getItems().stream()
				.map(item -> new StaffOrderResponse.OrderLineResponse(
					item.getMenuItem().getId(),
					item.getMenuItem().getItemName(),
					item.getQuantity(),
					item.getSubtotal()
				))
				.toList()
		);
	}
}
