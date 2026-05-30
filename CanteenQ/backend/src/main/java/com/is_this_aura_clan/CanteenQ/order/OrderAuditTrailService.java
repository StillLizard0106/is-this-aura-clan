package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class OrderAuditTrailService {

	private final OrderAuditTrailRepository auditTrailRepository;

	public OrderAuditTrailService(OrderAuditTrailRepository auditTrailRepository) {
		this.auditTrailRepository = Objects.requireNonNull(auditTrailRepository, "auditTrailRepository must not be null");
	}

	public void recordStatusChange(UUID orderId, OrderStatus previousStatus, OrderStatus newStatus, String changedBy) {
		Objects.requireNonNull(orderId, "orderId must not be null");
		Objects.requireNonNull(previousStatus, "previousStatus must not be null");
		Objects.requireNonNull(newStatus, "newStatus must not be null");

		OrderAuditTrail auditEntry = new OrderAuditTrail(orderId, previousStatus, newStatus, changedBy);
		auditTrailRepository.save(auditEntry);
	}

	public List<OrderAuditTrail> getAuditTrail(UUID orderId) {
		Objects.requireNonNull(orderId, "orderId must not be null");
		return auditTrailRepository.findByOrderIdOrderByChangedAtDesc(orderId);
	}
}
