package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@ExtendWith(MockitoExtension.class)
class StaffOrderAuditServiceTest {

	@Mock
	private UserAuthorizationService userAuthorizationService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderAuditTrailService auditTrailService;

	@Mock
	private OrderAuditTrailResponseMapper auditTrailResponseMapper;

	@InjectMocks
	private StaffOrderAuditService staffOrderAuditService;

	@Test
	void testGetOrderAuditTrail() {
		UUID orderId = UUID.randomUUID();
		FirebaseAuthenticationPrincipal principal = createTestPrincipal();
		CanteenOrder order = mock(CanteenOrder.class);

		List<OrderAuditTrail> auditTrails = List.of(
			new OrderAuditTrail(orderId, OrderStatus.PENDING, OrderStatus.PREPARING, "staff@school.edu")
		);

		List<OrderAuditTrailResponse> responses = List.of(
			new OrderAuditTrailResponse(UUID.randomUUID(), orderId, "PENDING", "PREPARING", "staff@school.edu", null)
		);

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(auditTrailService.getAuditTrail(orderId)).thenReturn(auditTrails);
		when(auditTrailResponseMapper.toResponseList(auditTrails)).thenReturn(responses);

		List<OrderAuditTrailResponse> result = staffOrderAuditService.getOrderAuditTrail(principal, orderId);

		assertEquals(1, result.size());
		verify(userAuthorizationService).requireRole(principal, UserRole.STAFF);
		verify(orderRepository).findById(orderId);
		verify(auditTrailService).getAuditTrail(orderId);
	}

	@Test
	void testGetOrderAuditTrailThrowsOrderNotFound() {
		UUID orderId = UUID.randomUUID();
		FirebaseAuthenticationPrincipal principal = createTestPrincipal();

		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		assertThrows(OrderNotFoundException.class, () ->
			staffOrderAuditService.getOrderAuditTrail(principal, orderId)
		);
	}

	@Test
	void testGetOrderAuditTrailRequiresStaffRole() {
		UUID orderId = UUID.randomUUID();
		FirebaseAuthenticationPrincipal principal = createTestPrincipal();

		doThrow(new RuntimeException("User is not staff"))
			.when(userAuthorizationService).requireRole(principal, UserRole.STAFF);

		assertThrows(RuntimeException.class, () ->
			staffOrderAuditService.getOrderAuditTrail(principal, orderId)
		);
	}

	private FirebaseAuthenticationPrincipal createTestPrincipal() {
		return new FirebaseAuthenticationPrincipal(
			"test-uid",
			"student@school.edu"
		);
	}
}
