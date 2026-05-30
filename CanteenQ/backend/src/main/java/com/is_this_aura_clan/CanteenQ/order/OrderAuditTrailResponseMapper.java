package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class OrderAuditTrailResponseMapper {

	public OrderAuditTrailResponse toResponse(OrderAuditTrail auditTrail) {
		return new OrderAuditTrailResponse(
			auditTrail.getId(),
			auditTrail.getOrderId(),
			auditTrail.getPreviousStatus().toString(),
			auditTrail.getNewStatus().toString(),
			auditTrail.getChangedBy(),
			auditTrail.getChangedAt()
		);
	}

	public List<OrderAuditTrailResponse> toResponseList(List<OrderAuditTrail> auditTrails) {
		return auditTrails.stream()
			.map(this::toResponse)
			.toList();
	}
}
