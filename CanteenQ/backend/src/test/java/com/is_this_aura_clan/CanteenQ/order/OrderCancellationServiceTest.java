package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;

class OrderCancellationServiceTest {

	private final Clock fixedClock = Clock.fixed(Instant.parse("2026-05-30T07:30:00Z"), ZoneOffset.UTC);

	@Test
	void cancelMarksPendingOrderAsCancelled() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderResponseMapper orderResponseMapper = new OrderResponseMapper();
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		CanteenOrder order = new CanteenOrder(student, stall, new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		order.addItem(new OrderItem(order, menuItem, 1, new BigDecimal("45.00")));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(userAccountRepository.findByFirebaseUid("uid-student")).thenReturn(Optional.of(student));
		when(orderRepository.findByIdAndStudent_Id(order.getId(), student.getId())).thenReturn(Optional.of(order));
		when(orderRepository.save(order)).thenReturn(order);

		OrderCancellationService service = new OrderCancellationService(authorizationService, userAccountRepository, orderRepository, fixedClock, orderResponseMapper, orderNotificationService, auditTrailService);

		OrderResponse response = service.cancel(new FirebaseAuthenticationPrincipal("uid-student", "jane@school.edu"), order.getId());

		assertEquals(OrderStatus.CANCELLED, response.status());
		assertEquals(order.getId(), response.id());
	}

	@Test
	void cancelRejectsLateCancellation() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderResponseMapper orderResponseMapper = new OrderResponseMapper();
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		CanteenOrder order = new CanteenOrder(student, stall, new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 7, 40), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(userAccountRepository.findByFirebaseUid("uid-student")).thenReturn(Optional.of(student));
		when(orderRepository.findByIdAndStudent_Id(order.getId(), student.getId())).thenReturn(Optional.of(order));

		OrderCancellationService service = new OrderCancellationService(authorizationService, userAccountRepository, orderRepository, fixedClock, orderResponseMapper, orderNotificationService, auditTrailService);

		assertThrows(
			OrderActionException.class,
			() -> service.cancel(new FirebaseAuthenticationPrincipal("uid-student", "jane@school.edu"), order.getId())
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
