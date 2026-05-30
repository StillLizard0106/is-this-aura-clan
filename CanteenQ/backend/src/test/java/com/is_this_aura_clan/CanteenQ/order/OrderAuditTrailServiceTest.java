package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAuditTrailServiceTest {

	@Mock
	private OrderAuditTrailRepository auditTrailRepository;

	@InjectMocks
	private OrderAuditTrailService auditTrailService;

	@Test
	void testRecordStatusChangeWithChangedBy() {
		UUID orderId = UUID.randomUUID();
		OrderStatus previousStatus = OrderStatus.PENDING;
		OrderStatus newStatus = OrderStatus.PREPARING;
		String changedBy = "staff@school.edu";

		auditTrailService.recordStatusChange(orderId, previousStatus, newStatus, changedBy);

		verify(auditTrailRepository, times(1)).save(any(OrderAuditTrail.class));
	}

	@Test
	void testRecordStatusChangeWithoutChangedBy() {
		UUID orderId = UUID.randomUUID();
		OrderStatus previousStatus = OrderStatus.READY;
		OrderStatus newStatus = OrderStatus.UNCLAIMED;

		auditTrailService.recordStatusChange(orderId, previousStatus, newStatus, null);

		verify(auditTrailRepository, times(1)).save(any(OrderAuditTrail.class));
	}

	@Test
	void testRecordStatusChangeRequiresOrderId() {
		assertThrows(NullPointerException.class, () ->
			auditTrailService.recordStatusChange(null, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu")
		);
	}

	@Test
	void testRecordStatusChangeRequiresPreviousStatus() {
		assertThrows(NullPointerException.class, () ->
			auditTrailService.recordStatusChange(UUID.randomUUID(), null, OrderStatus.PREPARING, "staff@school.edu")
		);
	}

	@Test
	void testRecordStatusChangeRequiresNewStatus() {
		assertThrows(NullPointerException.class, () ->
			auditTrailService.recordStatusChange(UUID.randomUUID(), OrderStatus.PENDING, null, "staff@school.edu")
		);
	}

	@Test
	void testGetAuditTrail() {
		UUID orderId = UUID.randomUUID();
		List<OrderAuditTrail> mockAuditTrail = List.of(
			new OrderAuditTrail(orderId, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu"),
			new OrderAuditTrail(orderId, OrderStatus.PREPARING, OrderStatus.READY, "staff@school.edu")
		);
		when(auditTrailRepository.findByOrderIdOrderByChangedAtDesc(orderId)).thenReturn(mockAuditTrail);

		List<OrderAuditTrail> result = auditTrailService.getAuditTrail(orderId);

		assertEquals(2, result.size());
		verify(auditTrailRepository, times(1)).findByOrderIdOrderByChangedAtDesc(orderId);
	}

	@Test
	void testGetAuditTrailRequiresOrderId() {
		assertThrows(NullPointerException.class, () ->
			auditTrailService.getAuditTrail(null)
		);
	}
}
