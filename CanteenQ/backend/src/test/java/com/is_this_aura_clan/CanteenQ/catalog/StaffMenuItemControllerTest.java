package com.is_this_aura_clan.CanteenQ.catalog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthInterceptor;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthService;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationResult;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

class StaffMenuItemControllerTest {

	@Test
	void listReturnsMenuItemsForStaff() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		MenuItemManagementService menuItemManagementService = mock(MenuItemManagementService.class);
		UUID stallId = UUID.fromString("55555555-5555-5555-5555-555555555555");
		when(menuItemManagementService.listMenuItems(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId)).thenReturn(
			List.of(new MenuItemResponse(UUID.fromString("66666666-6666-6666-6666-666666666666"), "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true))
		);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffMenuItemController(menuItemManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(get("/api/staff/stalls/{stallId}/menu-items", stallId).header("Authorization", "Bearer staff-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].itemName").value("Chicken Rice"));

		verify(authService).authenticate("Bearer staff-token");
	}

	@Test
	void createReturnsCreatedMenuItem() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		MenuItemManagementService menuItemManagementService = mock(MenuItemManagementService.class);
		UUID stallId = UUID.fromString("77777777-7777-7777-7777-777777777777");
		when(menuItemManagementService.createMenuItem(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			new MenuItemRequest("Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true)
		)).thenReturn(new MenuItemResponse(UUID.fromString("88888888-8888-8888-8888-888888888888"), "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffMenuItemController(menuItemManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(post("/api/staff/stalls/{stallId}/menu-items", stallId)
				.header("Authorization", "Bearer staff-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"itemName\":\"Chicken Rice\",\"description\":\"Rice with chicken\",\"price\":45.00,\"category\":\"Meals\",\"available\":true}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.itemName").value("Chicken Rice"));
	}

	@Test
	void updateReturnsUpdatedMenuItem() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		UUID stallId = UUID.fromString("99999999-9999-9999-9999-999999999999");
		UUID menuItemId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
		MenuItemManagementService menuItemManagementService = mock(MenuItemManagementService.class);
		when(menuItemManagementService.updateMenuItem(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			menuItemId,
			new MenuItemRequest("BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals", false)
		)).thenReturn(new MenuItemResponse(menuItemId, "BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals", false));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffMenuItemController(menuItemManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(put("/api/staff/stalls/{stallId}/menu-items/{menuItemId}", stallId, menuItemId)
				.header("Authorization", "Bearer staff-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"itemName\":\"BBQ Rice\",\"description\":\"Rice with barbecue\",\"price\":50.00,\"category\":\"Meals\",\"available\":false}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.itemName").value("BBQ Rice"))
			.andExpect(jsonPath("$.available").value(false));
	}

	@Test
	void deleteReturnsNoContent() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer staff-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), "Firebase token accepted")
		);

		UUID stallId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
		UUID menuItemId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
		MenuItemManagementService menuItemManagementService = mock(MenuItemManagementService.class);

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffMenuItemController(menuItemManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(delete("/api/staff/stalls/{stallId}/menu-items/{menuItemId}", stallId, menuItemId)
				.header("Authorization", "Bearer staff-token"))
			.andExpect(status().isNoContent());

		verify(menuItemManagementService).deleteMenuItem(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId, menuItemId);
	}

	@Test
	void createReturnsForbiddenForStudentAccount() throws Exception {
		FirebaseAuthService authService = mock(FirebaseAuthService.class);
		when(authService.authenticate("Bearer student-token")).thenReturn(
			new FirebaseAuthenticationResult(true, new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), "Firebase token accepted")
		);

		MenuItemManagementService menuItemManagementService = mock(MenuItemManagementService.class);
		UUID stallId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
		when(menuItemManagementService.createMenuItem(
			new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"),
			stallId,
			new MenuItemRequest("Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true)
		)).thenThrow(new InsufficientRoleException("Access requires STAFF role."));

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StaffMenuItemController(menuItemManagementService))
			.addInterceptors(new FirebaseAuthInterceptor(authService))
			.setControllerAdvice(new com.is_this_aura_clan.CanteenQ.auth.AuthExceptionHandler(), new CatalogExceptionHandler())
			.build();

		mockMvc.perform(post("/api/staff/stalls/{stallId}/menu-items", stallId)
				.header("Authorization", "Bearer student-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"itemName\":\"Chicken Rice\",\"description\":\"Rice with chicken\",\"price\":45.00,\"category\":\"Meals\",\"available\":true}"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("FORBIDDEN_ROLE"));
	}
}
