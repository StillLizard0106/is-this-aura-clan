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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItemRepository;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;

class OrderServiceTest {

	private final Clock fixedClock = Clock.fixed(Instant.parse("2026-05-30T08:00:00Z"), ZoneOffset.UTC);

	@Test
	void placeOrderCreatesPendingOrder() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderResponseMapper orderResponseMapper = new OrderResponseMapper();

		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		assignMenuItemId(menuItem, UUID.fromString("33333333-3333-3333-3333-333333333333"));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(userAccountRepository.findByFirebaseUid("uid-student")).thenReturn(Optional.of(student));
		when(stallRepository.existsById(stall.getId())).thenReturn(true);
		when(orderRepository.existsByStudent_IdAndStallIdAndStatusIn(student.getId(), stall.getId(), List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)))
			.thenReturn(false);
		when(menuItemRepository.findByIdAndStall_Id(menuItem.getId(), stall.getId())).thenReturn(Optional.of(menuItem));
		when(orderRepository.findTopByStallIdAndPickupSlotBetweenOrderByQueueNumberDesc(any(), any(), any())).thenReturn(Optional.empty());
		when(orderRepository.save(any(CanteenOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

		OrderService service = new OrderService(authorizationService, userAccountRepository, stallRepository, menuItemRepository, orderRepository, fixedClock, orderResponseMapper);

		OrderResponse response = service.placeOrder(
			new FirebaseAuthenticationPrincipal("uid-student", "jane@school.edu"),
			new OrderRequest(
				stall.getId(),
				LocalDateTime.of(2026, 5, 30, 8, 20),
				List.of(new OrderRequest.OrderLineRequest(menuItem.getId(), 2))
			)
		);

		assertEquals(stall.getId(), response.stallId());
		assertEquals(new BigDecimal("90.00"), response.totalPrice());
		assertEquals(1, response.queueNumber());
		assertEquals(OrderStatus.PENDING, response.status());
		assertEquals("Chicken Rice", response.items().get(0).itemName());
	}

	@Test
	void placeOrderRejectsPickupSlotThatIsTooSoon() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderResponseMapper orderResponseMapper = new OrderResponseMapper();

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(null);

		OrderService service = new OrderService(authorizationService, userAccountRepository, stallRepository, menuItemRepository, orderRepository, fixedClock, orderResponseMapper);

		assertThrows(
			OrderPlacementException.class,
			() -> service.placeOrder(
				new FirebaseAuthenticationPrincipal("uid-student", "jane@school.edu"),
				new OrderRequest(UUID.randomUUID(), LocalDateTime.of(2026, 5, 30, 8, 10), List.of(new OrderRequest.OrderLineRequest(UUID.randomUUID(), 1)))
			)
		);
	}

	@Test
	void placeOrderRejectsActiveOrderConflict() {
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		OrderRepository orderRepository = mock(OrderRepository.class);
		OrderResponseMapper orderResponseMapper = new OrderResponseMapper();

		UserAccount student = new UserAccount("Jane Doe", "2024-0001", "jane@school.edu", "uid-student", UserRole.STUDENT);
		assignId(student, UUID.fromString("11111111-1111-1111-1111-111111111111"));
		Stall stall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(stall, UUID.fromString("22222222-2222-2222-2222-222222222222"));

		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), any())).thenReturn(student);
		when(userAccountRepository.findByFirebaseUid("uid-student")).thenReturn(Optional.of(student));
		when(stallRepository.existsById(stall.getId())).thenReturn(true);
		when(orderRepository.existsByStudent_IdAndStallIdAndStatusIn(student.getId(), stall.getId(), List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY)))
			.thenReturn(true);

		OrderService service = new OrderService(authorizationService, userAccountRepository, stallRepository, menuItemRepository, orderRepository, fixedClock, orderResponseMapper);

		assertThrows(
			OrderConflictException.class,
			() -> service.placeOrder(
				new FirebaseAuthenticationPrincipal("uid-student", "jane@school.edu"),
				new OrderRequest(stall.getId(), LocalDateTime.of(2026, 5, 30, 8, 20), List.of(new OrderRequest.OrderLineRequest(UUID.randomUUID(), 1)))
			)
		);
	}

	private void assignId(UserAccount userAccount, UUID id) {
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
}
