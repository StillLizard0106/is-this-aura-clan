package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallNotFoundException;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;

class StaffOrderQueueServiceTest {

	@Test
	void getQueueReturnsActiveOrdersForStaff() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		StallRepository stallRepository = mock(StallRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();

		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		CanteenOrder order = new CanteenOrder(student, stall, new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		order.addItem(new OrderItem(order, menuItem, 1, new BigDecimal("45.00")));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(stallRepository.existsById(stall.getId())).thenReturn(true);
		when(orderRepository.findByStall_IdAndStatusInOrderByPickupSlotAscQueueNumberAsc(stall.getId(), List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)))
			.thenReturn(List.of(order));

		StaffOrderQueueService service = new StaffOrderQueueService(authorizationService, stallRepository, orderRepository, staffOrderResponseMapper);

		StaffOrderQueueResponse response = service.getQueue(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stall.getId());

		assertEquals(stall.getId(), response.stallId());
		assertEquals(1, response.orders().size());
		assertEquals("Jane Doe", response.orders().get(0).studentName());
		assertEquals("Chicken Rice", response.orders().get(0).items().get(0).itemName());
	}

	@Test
	void getQueueRejectsMissingStall() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		StallRepository stallRepository = mock(StallRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();
		UUID stallId = UUID.randomUUID();

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(null);
		when(stallRepository.existsById(stallId)).thenReturn(false);

		StaffOrderQueueService service = new StaffOrderQueueService(authorizationService, stallRepository, orderRepository, staffOrderResponseMapper);

		assertThrows(
			StallNotFoundException.class,
			() -> service.getQueue(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId)
		);
	}

	@Test
	void getQueueFiltersByDateAndStatus() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		StallRepository stallRepository = mock(StallRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		StaffOrderResponseMapper staffOrderResponseMapper = new StaffOrderResponseMapper();

		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignUserId(student, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		CanteenOrder order = new CanteenOrder(student, stall, new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(order, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		order.updateStatus(OrderStatus.PREPARING);
		order.updateStatus(OrderStatus.READY);
		order.updateStatus(OrderStatus.COMPLETED);
		order.addItem(new OrderItem(order, menuItem, 1, new BigDecimal("45.00")));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(stallRepository.existsById(stall.getId())).thenReturn(true);
		when(orderRepository.findByStall_IdAndPickupSlotBetweenAndStatusInOrderByPickupSlotAscQueueNumberAsc(
			stall.getId(),
			LocalDate.of(2026, 5, 30).atStartOfDay(),
			LocalDate.of(2026, 5, 30).atTime(java.time.LocalTime.MAX),
			List.of(OrderStatus.COMPLETED)
		)).thenReturn(List.of(order));

		StaffOrderQueueService service = new StaffOrderQueueService(authorizationService, stallRepository, orderRepository, staffOrderResponseMapper);

		StaffOrderQueueResponse response = service.getQueue(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stall.getId(),
			LocalDate.of(2026, 5, 30),
			List.of(OrderStatus.COMPLETED)
		);

		assertEquals(1, response.orders().size());
		assertEquals(OrderStatus.COMPLETED, response.orders().get(0).status());
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
