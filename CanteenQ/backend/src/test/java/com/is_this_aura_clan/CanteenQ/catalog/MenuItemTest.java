package com.is_this_aura_clan.CanteenQ.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MenuItemTest {

	@Test
	void constructorInitializesMenuItemFields() {
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);

		assertNull(menuItem.getId());
		assertEquals(stall, menuItem.getStall());
		assertEquals("Chicken Rice", menuItem.getItemName());
		assertEquals("Rice with chicken", menuItem.getDescription());
		assertEquals(new BigDecimal("45.00"), menuItem.getPrice());
		assertEquals("Meals", menuItem.getCategory());
	}

	@Test
	void updateDetailsAndAvailabilityChangeMutableFields() {
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		MenuItem menuItem = new MenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);

		menuItem.updateDetails("BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals");
		menuItem.setAvailable(false);

		assertEquals("BBQ Rice", menuItem.getItemName());
		assertEquals("Rice with barbecue", menuItem.getDescription());
		assertEquals(new BigDecimal("50.00"), menuItem.getPrice());
		assertFalse(menuItem.isAvailable());
	}
}
