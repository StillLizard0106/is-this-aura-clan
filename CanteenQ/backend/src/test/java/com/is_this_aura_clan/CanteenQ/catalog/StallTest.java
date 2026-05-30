package com.is_this_aura_clan.CanteenQ.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class StallTest {

	@Test
	void constructorInitializesStallFields() {
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");

		assertNull(stall.getId());
		assertEquals("Rice Bowl", stall.getStallName());
		assertEquals("A. Vendor", stall.getVendorName());
		assertEquals("8:00 AM - 2:00 PM", stall.getOperatingHours());
	}

	@Test
	void updateDetailsChangesMutableFields() {
		Stall stall = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");

		stall.updateDetails("Snack Corner", "B. Vendor", "9:00 AM - 3:00 PM");

		assertEquals("Snack Corner", stall.getStallName());
		assertEquals("B. Vendor", stall.getVendorName());
		assertEquals("9:00 AM - 3:00 PM", stall.getOperatingHours());
	}
}
