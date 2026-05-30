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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@ExtendWith(MockitoExtension.class)
class StaffOrderAuditControllerTest {

	@Mock
	private StaffOrderAuditService staffOrderAuditService;

	@InjectMocks
	private StaffOrderAuditController staffOrderAuditController;

	@Test
	void testGetOrderAuditTrail() {
		UUID orderId = UUID.randomUUID();
		FirebaseAuthenticationPrincipal principal = createTestPrincipal();

		List<OrderAuditTrailResponse> auditTrail = List.of(
			new OrderAuditTrailResponse(UUID.randomUUID(), orderId, "PENDING", "PREPARING", "staff@school.edu", null)
		);

		when(staffOrderAuditService.getOrderAuditTrail(principal, orderId)).thenReturn(auditTrail);

		ResponseEntity<List<OrderAuditTrailResponse>> response = staffOrderAuditController.getOrderAuditTrail(principal, orderId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(1, response.getBody().size());
		verify(staffOrderAuditService).getOrderAuditTrail(principal, orderId);
	}

	private FirebaseAuthenticationPrincipal createTestPrincipal() {
		return new FirebaseAuthenticationPrincipal(
			"test-uid",
			"staff@school.edu"
		);
	}
}

