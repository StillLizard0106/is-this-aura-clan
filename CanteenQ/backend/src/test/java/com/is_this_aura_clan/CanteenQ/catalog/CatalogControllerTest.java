package com.is_this_aura_clan.CanteenQ.catalog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CatalogControllerTest {

	@Test
	void stallsEndpointReturnsAllStalls() throws Exception {
		CatalogService service = mock(CatalogService.class);
		when(service.listStalls()).thenReturn(
			List.of(new StallResponse(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM"))
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CatalogController(service))
			.setControllerAdvice(new CatalogExceptionHandler())
			.build();

		mockMvc.perform(get("/api/stalls"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].stallName").value("Rice Bowl"))
			.andExpect(jsonPath("$[0].vendorName").value("A. Vendor"));

		verify(service).listStalls();
	}

	@Test
	void menuItemsEndpointReturnsItemsForKnownStall() throws Exception {
		CatalogService service = mock(CatalogService.class);
		UUID stallId = UUID.fromString("22222222-2222-2222-2222-222222222222");
		when(service.listMenuItems(stallId)).thenReturn(
			List.of(new MenuItemResponse(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true))
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CatalogController(service))
			.setControllerAdvice(new CatalogExceptionHandler())
			.build();

		mockMvc.perform(get("/api/stalls/{stallId}/menu-items", stallId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].itemName").value("Chicken Rice"))
			.andExpect(jsonPath("$[0].price").value(45.00));

		verify(service).listMenuItems(stallId);
	}

	@Test
	void menuItemsEndpointReturnsNotFoundForUnknownStall() throws Exception {
		CatalogService service = mock(CatalogService.class);
		UUID stallId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		when(service.listMenuItems(stallId)).thenThrow(new StallNotFoundException("No stall found for id " + stallId));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new CatalogController(service))
			.setControllerAdvice(new CatalogExceptionHandler())
			.build();

		mockMvc.perform(get("/api/stalls/{stallId}/menu-items", stallId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("STALL_NOT_FOUND"));
	}
}
