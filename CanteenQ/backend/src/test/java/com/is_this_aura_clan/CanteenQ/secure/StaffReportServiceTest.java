package com.is_this_aura_clan.CanteenQ.secure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;
import com.is_this_aura_clan.CanteenQ.order.CanteenOrder;
import com.is_this_aura_clan.CanteenQ.order.OrderItem;
import com.is_this_aura_clan.CanteenQ.order.OrderRepository;
import com.is_this_aura_clan.CanteenQ.order.OrderStatus;

class StaffReportServiceTest {

	@Test
	void getSummaryBuildsCountsAndStallBreakdownsFromOrderRepository() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		StallRepository stallRepository = mock(StallRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		Clock clock = Clock.fixed(Instant.parse("2026-05-30T08:00:00Z"), ZoneOffset.UTC);

		UserAccount staff = new UserAccount("Staff One", "S-1001", "staff@school.edu", "uid-staff", UserRole.STAFF);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any(UserRole.class))).thenReturn(staff);

		Stall riceBowl = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(riceBowl, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall noodleHouse = new Stall("Noodle House", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(noodleHouse, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		when(stallRepository.count()).thenReturn(2L);
		when(stallRepository.findAll()).thenReturn(List.of(riceBowl, noodleHouse));

		UserAccount studentOne = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student-1", UserRole.STUDENT);
		assignUserId(studentOne, UUID.fromString("33333333-3333-3333-3333-333333333333"));
		UserAccount studentTwo = new UserAccount("John Doe", "2024-0002", "john@school.edu", "uid-student-2", UserRole.STUDENT);
		assignUserId(studentTwo, UUID.fromString("44444444-4444-4444-4444-444444444444"));
		MenuItem menuItem = new MenuItem(riceBowl, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("55555555-5555-5555-5555-555555555555"));

		CanteenOrder riceOrderOne = new CanteenOrder(studentOne, riceBowl.getId(), new BigDecimal("45.00"), LocalDateTime.of(2026, 5, 30, 8, 20), 1);
		assignOrderId(riceOrderOne, UUID.fromString("66666666-6666-6666-6666-666666666666"));
		riceOrderOne.addItem(new OrderItem(riceOrderOne, menuItem, 1, new BigDecimal("45.00")));

		CanteenOrder riceOrderTwo = new CanteenOrder(studentTwo, riceBowl.getId(), new BigDecimal("30.00"), LocalDateTime.of(2026, 5, 30, 9, 20), 2);
		assignOrderId(riceOrderTwo, UUID.fromString("77777777-7777-7777-7777-777777777777"));
		riceOrderTwo.addItem(new OrderItem(riceOrderTwo, menuItem, 1, new BigDecimal("30.00")));

		CanteenOrder noodleOrder = new CanteenOrder(studentOne, noodleHouse.getId(), new BigDecimal("90.00"), LocalDateTime.of(2026, 5, 30, 10, 20), 3);
		assignOrderId(noodleOrder, UUID.fromString("88888888-8888-8888-8888-888888888888"));
		noodleOrder.addItem(new OrderItem(noodleOrder, menuItem, 2, new BigDecimal("90.00")));

		when(orderRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(riceOrderOne, riceOrderTwo, noodleOrder));
		when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(2L);
		when(orderRepository.countByStatus(OrderStatus.PREPARING)).thenReturn(3L);
		when(orderRepository.countByStatus(OrderStatus.READY)).thenReturn(4L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(5L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.CANCELLED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.UNCLAIMED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(2L);

		StaffReportService service = new StaffReportService(authorizationService, stallRepository, orderRepository, clock);

		StaffReportResponse response = service.getSummary(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), null, null);

		assertEquals(2L, response.totalStalls());
		assertEquals(3L, response.totalOrders());
		assertEquals(3L, response.ordersToday());
		assertEquals(9L, response.activeOrders());
		assertEquals(2L, response.pendingOrders());
		assertEquals(3L, response.preparingOrders());
		assertEquals(4L, response.readyOrders());
		assertEquals(5L, response.completedToday());
		assertEquals(1L, response.cancelledToday());
		assertEquals(2L, response.unclaimedToday());
		assertEquals(2, response.stallBreakdowns().size());
		assertEquals("Rice Bowl", response.stallBreakdowns().get(0).stallName());
		assertEquals(2L, response.stallBreakdowns().get(0).orderCount());
		assertEquals(new BigDecimal("75.00"), response.stallBreakdowns().get(0).totalRevenue());
		assertEquals("Noodle House", response.stallBreakdowns().get(1).stallName());
		assertEquals(1L, response.stallBreakdowns().get(1).orderCount());
		assertEquals(new BigDecimal("90.00"), response.stallBreakdowns().get(1).totalRevenue());
	}

	@Test
	void getSummaryUsesProvidedDateRange() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		StallRepository stallRepository = mock(StallRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		Clock clock = Clock.fixed(Instant.parse("2026-05-30T08:00:00Z"), ZoneOffset.UTC);

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any(UserRole.class))).thenReturn(
			new com.is_this_aura_clan.CanteenQ.account.UserAccount("Staff One", "S-1001", "staff@school.edu", "uid-staff", UserRole.STAFF)
		);
		when(stallRepository.count()).thenReturn(1L);
		when(stallRepository.findAll()).thenReturn(List.of());
		when(orderRepository.findByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of());
		when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(1L);
		when(orderRepository.countByStatus(OrderStatus.PREPARING)).thenReturn(1L);
		when(orderRepository.countByStatus(OrderStatus.READY)).thenReturn(1L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(2L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.CANCELLED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(0L);
		when(orderRepository.countByStatusAndUpdatedAtBetween(eq(OrderStatus.UNCLAIMED), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(1L);

		StaffReportService service = new StaffReportService(authorizationService, stallRepository, orderRepository, clock);

		StaffReportResponse response = service.getSummary(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			LocalDate.of(2026, 5, 28),
			LocalDate.of(2026, 5, 29)
		);

		assertEquals(0L, response.totalOrders());
		assertEquals(0L, response.ordersToday());
		assertEquals(3L, response.activeOrders());
		assertEquals(0, response.stallBreakdowns().size());
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
