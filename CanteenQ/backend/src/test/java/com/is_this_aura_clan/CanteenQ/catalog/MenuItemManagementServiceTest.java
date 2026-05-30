package com.is_this_aura_clan.CanteenQ.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

class MenuItemManagementServiceTest {

	@Test
	void createMenuItemSavesNewItemForStaff() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF))).thenReturn(null);
		UUID stallId = UUID.randomUUID();
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(stall));
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stallId, "Chicken Rice")).thenReturn(Optional.empty());
		when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MenuItemManagementService service = new MenuItemManagementService(stallRepository, menuItemRepository, authorizationService);

		MenuItemResponse response = service.createMenuItem(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			new MenuItemRequest("Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true)
		);

		assertEquals("Chicken Rice", response.itemName());
		assertEquals(new BigDecimal("45.00"), response.price());
	}

	@Test
	void updateMenuItemChangesExistingItem() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF))).thenReturn(null);
		UUID stallId = UUID.randomUUID();
		UUID menuItemId = UUID.randomUUID();
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		MenuItem existing = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(stall));
		when(menuItemRepository.findByIdAndStall_Id(menuItemId, stallId)).thenReturn(Optional.of(existing));
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stallId, "BBQ Rice")).thenReturn(Optional.empty());
		when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MenuItemManagementService service = new MenuItemManagementService(stallRepository, menuItemRepository, authorizationService);

		MenuItemResponse response = service.updateMenuItem(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			menuItemId,
			new MenuItemRequest("BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals", false)
		);

		assertEquals("BBQ Rice", response.itemName());
		assertEquals(new BigDecimal("50.00"), response.price());
		assertFalse(response.available());
	}

	@Test
	void deleteMenuItemRemovesExistingItem() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF))).thenReturn(null);
		UUID stallId = UUID.randomUUID();
		UUID menuItemId = UUID.randomUUID();
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		MenuItem existing = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(stall));
		when(menuItemRepository.findByIdAndStall_Id(menuItemId, stallId)).thenReturn(Optional.of(existing));

		MenuItemManagementService service = new MenuItemManagementService(stallRepository, menuItemRepository, authorizationService);

		service.deleteMenuItem(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId, menuItemId);

		org.mockito.Mockito.verify(menuItemRepository).delete(existing);
	}

	@Test
	void createMenuItemRejectsDuplicateNamesWithinTheSameStall() {
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF))).thenReturn(null);
		UUID stallId = UUID.randomUUID();
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(stall));
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stallId, "Chicken Rice"))
			.thenReturn(Optional.of(new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true)));

		MenuItemManagementService service = new MenuItemManagementService(stallRepository, menuItemRepository, authorizationService);

		assertThrows(
			DuplicateMenuItemException.class,
			() -> service.createMenuItem(
				new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
				stallId,
				new MenuItemRequest("Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true)
			)
		);
	}
}
