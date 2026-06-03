package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;

class StaffOrderStatusServiceTest {

	@Test
	void updateStatusAdvancesOrderState() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount staff = new UserAccount("Staff One", "S-1001", "staff@school.edu", "uid-staff", UserRole.STAFF);
		assignUserId(staff, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		CanteenOrder order = new CanteenOrder(staff, stall, new BigDecimal("45.00"), java.time.LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		order.addItem(new OrderItem(order, menuItem, 1, new BigDecimal("45.00")));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(staff);
		when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
		when(orderRepository.save(order)).thenReturn(order);

		StaffOrderStatusService service = new StaffOrderStatusService(authorizationService, orderRepository, staffOrderResponseMapper, orderNotificationService, auditTrailService);

		StaffOrderResponse response = service.updateStatus(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			order.getId(),
			new StaffOrderStatusUpdateRequest(OrderStatus.PREPARING)
		);

		assertEquals(OrderStatus.PREPARING, response.status());
		assertEquals(order.getId(), response.id());
	}

	@Test
	void updateStatusAllowsPendingCancellation() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount staff = new UserAccount("Staff One", "S-1001", "staff@school.edu", "uid-staff", UserRole.STAFF);
		assignUserId(staff, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		CanteenOrder order = new CanteenOrder(staff, stall, new BigDecimal("45.00"), java.time.LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		order.addItem(new OrderItem(order, menuItem, 1, new BigDecimal("45.00")));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(staff);
		when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
		when(orderRepository.save(order)).thenReturn(order);

		StaffOrderStatusService service = new StaffOrderStatusService(authorizationService, orderRepository, staffOrderResponseMapper, orderNotificationService, auditTrailService);

		StaffOrderResponse response = service.updateStatus(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			order.getId(),
			new StaffOrderStatusUpdateRequest(OrderStatus.CANCELLED)
		);

		assertEquals(OrderStatus.CANCELLED, response.status());
		assertEquals(order.getId(), response.id());
	}

	@Test
	void updateStatusRejectsInvalidTransition() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount staff = new UserAccount("Staff One", "S-1001", "staff@school.edu", "uid-staff", UserRole.STAFF);
		assignUserId(staff, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		CanteenOrder order = new CanteenOrder(staff, stall, new BigDecimal("45.00"), java.time.LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(staff);
		when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

		StaffOrderStatusService service = new StaffOrderStatusService(authorizationService, orderRepository, staffOrderResponseMapper, orderNotificationService, auditTrailService);

		assertThrows(
			OrderStatusTransitionException.class,
			() -> service.updateStatus(
				new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
				order.getId(),
				new StaffOrderStatusUpdateRequest(OrderStatus.COMPLETED)
			)
		);
	}

	private void assignUserId(UserAccount userAccount, UUID id) {
		try {
			java.lang.reflect.Field idField = UserAccount.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(userAccount, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void assignStallId(Stall stall, UUID id) {
		try {
			java.lang.reflect.Field idField = Stall.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(stall, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void assignMenuItemId(MenuItem menuItem, UUID id) {
		try {
			java.lang.reflect.Field idField = MenuItem.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(menuItem, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}

	private void assignOrderId(CanteenOrder order, UUID id) {
		try {
			java.lang.reflect.Field idField = CanteenOrder.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(order, id);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}
}
