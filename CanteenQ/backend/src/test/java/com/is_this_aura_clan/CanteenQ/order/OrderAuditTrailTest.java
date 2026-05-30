package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderAuditTrailTest {

	@Test
	void testOrderAuditTrailConstruction() {
		UUID orderId = UUID.randomUUID();
		OrderStatus previousStatus = OrderStatus.PENDING;
		OrderStatus newStatus = OrderStatus.PREPARING;
		String changedBy = "staff@school.edu";

		OrderAuditTrail auditTrail = new OrderAuditTrail(orderId, previousStatus, newStatus, changedBy);

		assertEquals(orderId, auditTrail.getOrderId());
		assertEquals(previousStatus, auditTrail.getPreviousStatus());
		assertEquals(newStatus, auditTrail.getNewStatus());
		assertEquals(changedBy, auditTrail.getChangedBy());
	}

	@Test
	void testOrderAuditTrailWithNullChangedBy() {
		UUID orderId = UUID.randomUUID();
		OrderStatus previousStatus = OrderStatus.READY;
		OrderStatus newStatus = OrderStatus.UNCLAIMED;

		OrderAuditTrail auditTrail = new OrderAuditTrail(orderId, previousStatus, newStatus, null);

		assertEquals(orderId, auditTrail.getOrderId());
		assertEquals(previousStatus, auditTrail.getPreviousStatus());
		assertEquals(newStatus, auditTrail.getNewStatus());
		assertNull(auditTrail.getChangedBy());
	}

	@Test
	void testOrderAuditTrailRequiresOrderId() {
		assertThrows(NullPointerException.class, () ->
			new OrderAuditTrail(null, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu")
		);
	}

	@Test
	void testOrderAuditTrailRequiresPreviousStatus() {
		assertThrows(NullPointerException.class, () ->
			new OrderAuditTrail(UUID.randomUUID(), null, OrderStatus.PREPARING, "staff@school.edu")
		);
	}

	@Test
	void testOrderAuditTrailRequiresNewStatus() {
		assertThrows(NullPointerException.class, () ->
			new OrderAuditTrail(UUID.randomUUID(), OrderStatus.PENDING, null, "staff@school.edu")
		);
	}

	@Test
	void testChangedAtIsSetOnConstruction() {
		OrderAuditTrail auditTrail = new OrderAuditTrail(
			UUID.randomUUID(),
			OrderStatus.PENDING,
			OrderStatus.PREPARING,
			"staff@school.edu"
		);

		assertNull(auditTrail.getChangedAt());
	}
}
