package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;

class OrderUnclaimedSchedulerTest {

	private final Clock fixedClock = Clock.fixed(Instant.parse("2026-05-30T09:20:00Z"), ZoneOffset.UTC);

	@Test
	void markExpiredOrdersAsUnclaimedUpdatesDueOrdersOnly() {
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);

		UserAccount studentOne = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student-1", UserRole.STUDENT);
		assignUserId(studentOne, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		UserAccount studentTwo = new UserAccount("John Doe", "2024-0002", "john@school.edu", "uid-student-2", UserRole.STUDENT);
		assignUserId(studentTwo, UUID.fromString("22222222-2222-2222-2222-222222222222"));

		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("44444444-4444-4444-4444-444444444444"));

		CanteenOrder dueOrder = new CanteenOrder(studentOne, stall.getId(), new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(dueOrder, UUID.fromString("55555555-5555-5555-5555-555555555555"));
		dueOrder.addItem(new OrderItem(dueOrder, menuItem, 1, new BigDecimal("45.00")));
		orderReady(dueOrder);

		CanteenOrder notDueOrder = new CanteenOrder(studentTwo, stall.getId(), new BigDecimal("90.00"), LocalDateTime.of(2026, 5, 30, 9, 10), 2);
		assignOrderId(notDueOrder, UUID.fromString("66666666-6666-6666-6666-666666666666"));
		notDueOrder.addItem(new OrderItem(notDueOrder, menuItem, 2, new BigDecimal("90.00")));
		orderReady(notDueOrder);

		when(orderRepository.findByStatusAndPickupSlotLessThanEqualOrderByPickupSlotAscQueueNumberAsc(OrderStatus.READY, LocalDateTime.of(2026, 5, 30, 9, 5)))
			.thenReturn(List.of(dueOrder));
		when(orderRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		OrderUnclaimedScheduler scheduler = new OrderUnclaimedScheduler(orderRepository, fixedClock, orderNotificationService, auditTrailService);
		scheduler.markExpiredOrdersAsUnclaimed();

		assertEquals(OrderStatus.UNCLAIMED, dueOrder.getStatus());
		assertEquals(OrderStatus.READY, notDueOrder.getStatus());
		verify(orderRepository).saveAll(List.of(dueOrder));
		verify(orderNotificationService).publishStudentOrderUpdateAfterCommit(
			dueOrder.getStudent().getId(),
			new OrderStatusNotification(dueOrder.getId(), OrderStatus.UNCLAIMED, "Your order was marked unclaimed.")
		);
	}

	@Test
	void markExpiredOrdersAsUnclaimedSkipsEmptyQueue() {
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderNotificationService orderNotificationService = mock(OrderNotificationService.class);
		OrderAuditTrailService auditTrailService = mock(OrderAuditTrailService.class);
		when(orderRepository.findByStatusAndPickupSlotLessThanEqualOrderByPickupSlotAscQueueNumberAsc(any(), any())).thenReturn(List.of());

		OrderUnclaimedScheduler scheduler = new OrderUnclaimedScheduler(orderRepository, fixedClock, orderNotificationService, auditTrailService);
		scheduler.markExpiredOrdersAsUnclaimed();

		verify(orderRepository).findByStatusAndPickupSlotLessThanEqualOrderByPickupSlotAscQueueNumberAsc(OrderStatus.READY, LocalDateTime.of(2026, 5, 30, 9, 5));
	}

	private void orderReady(CanteenOrder order) {
		order.updateStatus(OrderStatus.PREPARING);
		order.updateStatus(OrderStatus.READY);
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
