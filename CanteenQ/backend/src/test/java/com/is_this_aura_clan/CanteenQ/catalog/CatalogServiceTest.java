package com.is_this_aura_clan.CanteenQ.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CatalogServiceTest {

	@Test
	void listStallsReturnsMappedStalls() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		when(stallRepository.findAll()).thenReturn(List.of(stall));
		when(orderRepository.countByStallIdAndStatusIn(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(0L);

		CatalogService service = new CatalogService(stallRepository, menuItemRepository, orderRepository);

		List<StallResponse> stalls = service.listStalls();

		assertEquals(1, stalls.size());
		assertEquals("Rice Bowl", stalls.get(0).stallName());
		assertEquals(100, stalls.get(0).queueLimit());
		assertEquals(100, stalls.get(0).queueSlotsLeft());
	}

	@Test
	void listMenuItemsReturnsMappedItemsForExistingStall() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UUID stallId = UUID.randomUUID();
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);

		when(stallRepository.existsById(stallId)).thenReturn(true);
		when(menuItemRepository.findByStall_IdOrderByItemNameAsc(stallId)).thenReturn(List.of(menuItem));
		when(orderRepository.countByStallIdAndStatusIn(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(0L);

		CatalogService service = new CatalogService(stallRepository, menuItemRepository, orderRepository);

		List<MenuItemResponse> items = service.listMenuItems(stallId);

		assertEquals(1, items.size());
		assertEquals("Chicken Rice", items.get(0).itemName());
		assertEquals(new BigDecimal("45.00"), items.get(0).price());
	}

	@Test
	void listMenuItemsRejectsUnknownStall() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UUID stallId = UUID.randomUUID();
		when(stallRepository.existsById(stallId)).thenReturn(false);

		CatalogService service = new CatalogService(stallRepository, menuItemRepository, orderRepository);

		assertThrows(StallNotFoundException.class, () -> service.listMenuItems(stallId));
	}
}
