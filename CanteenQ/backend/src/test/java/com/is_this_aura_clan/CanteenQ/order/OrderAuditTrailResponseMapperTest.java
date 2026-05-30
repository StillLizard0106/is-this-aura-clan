package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OrderAuditTrailResponseMapperTest {

	private final OrderAuditTrailResponseMapper mapper = new OrderAuditTrailResponseMapper();

	@Test
	void testMapToResponse() {
		UUID id = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		OrderAuditTrail auditTrail = new OrderAuditTrail(orderId, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu");

		OrderAuditTrailResponse response = mapper.toResponse(auditTrail);

		assertEquals(orderId, response.orderId());
		assertEquals("PENDING", response.previousStatus());
		assertEquals("PREPARING", response.newStatus());
		assertEquals("staff@school.edu", response.changedBy());
	}

	@Test
	void testMapToResponseWithNullChangedBy() {
		UUID orderId = UUID.randomUUID();
		OrderAuditTrail auditTrail = new OrderAuditTrail(orderId, OrderStatus.READY, OrderStatus.UNCLAIMED, null);

		OrderAuditTrailResponse response = mapper.toResponse(auditTrail);

		assertEquals(orderId, response.orderId());
		assertEquals("READY", response.previousStatus());
		assertEquals("UNCLAIMED", response.newStatus());
		assertNull(response.changedBy());
	}

	@Test
	void testMapToResponseList() {
		UUID orderId = UUID.randomUUID();
		List<OrderAuditTrail> auditTrails = List.of(
			new OrderAuditTrail(orderId, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu"),
			new OrderAuditTrail(orderId, OrderStatus.PREPARING, OrderStatus.READY, "staff@school.edu")
		);

		List<OrderAuditTrailResponse> responses = mapper.toResponseList(auditTrails);

		assertEquals(2, responses.size());
		assertEquals("PENDING", responses.get(0).previousStatus());
		assertEquals("PREPARING", responses.get(1).previousStatus());
	}

	@Test
	void testMapToResponseListEmpty() {
		List<OrderAuditTrailResponse> responses = mapper.toResponseList(List.of());

		assertTrue(responses.isEmpty());
	}
}
