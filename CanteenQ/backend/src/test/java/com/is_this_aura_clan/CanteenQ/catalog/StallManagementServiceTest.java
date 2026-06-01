package com.is_this_aura_clan.CanteenQ.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

class StallManagementServiceTest {

	@Test
	void createStallSavesNewStallForStaff() {
		StallRepository stallRepository = mock(StallRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF)))
			.thenReturn(null);
		when(stallRepository.findByStallNameIgnoreCase("Rice Bowl")).thenReturn(Optional.empty());
		when(stallRepository.save(any(Stall.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(orderRepository.countByStallIdAndStatusIn(any(), any())).thenReturn(0L);

		StallManagementService service = new StallManagementService(stallRepository, authorizationService, orderRepository);

		StallResponse response = service.createStall(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			new StallRequest("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM")
		);

		assertEquals("Rice Bowl", response.stallName());
		assertEquals("A. Vendor", response.vendorName());
		assertEquals(100, response.queueLimit());
		assertEquals(100, response.queueSlotsLeft());
	}

	@Test
	void updateStallChangesExistingStall() {
		StallRepository stallRepository = mock(StallRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF)))
			.thenReturn(null);
		UUID stallId = UUID.randomUUID();
		Stall existing = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(existing));
		when(stallRepository.findByStallNameIgnoreCase("Snack Corner")).thenReturn(Optional.empty());
		when(stallRepository.save(any(Stall.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(orderRepository.countByStallIdAndStatusIn(any(), any())).thenReturn(0L);

		StallManagementService service = new StallManagementService(stallRepository, authorizationService, orderRepository);

		StallResponse response = service.updateStall(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			stallId,
			new StallRequest("Snack Corner", "B. Vendor", "9:00 AM - 3:00 PM")
		);

		assertEquals("Snack Corner", response.stallName());
		assertEquals("B. Vendor", response.vendorName());
		assertEquals(100, response.queueLimit());
		assertEquals(100, response.queueSlotsLeft());
	}

	@Test
	void deleteStallRemovesExistingStall() {
		StallRepository stallRepository = mock(StallRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF)))
			.thenReturn(null);
		UUID stallId = UUID.randomUUID();
		Stall existing = new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM");
		when(stallRepository.findById(stallId)).thenReturn(Optional.of(existing));
		when(orderRepository.countByStallIdAndStatusIn(any(), any())).thenReturn(0L);

		StallManagementService service = new StallManagementService(stallRepository, authorizationService, orderRepository);

		service.deleteStall(new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"), stallId);

		org.mockito.Mockito.verify(stallRepository).delete(existing);
	}

	@Test
	void createStallRejectsDuplicateNames() {
		StallRepository stallRepository = mock(StallRepository.class);
		com.is_this_aura_clan.CanteenQ.order.OrderRepository orderRepository = mock(com.is_this_aura_clan.CanteenQ.order.OrderRepository.class);
		UserAuthorizationService authorizationService = mock(UserAuthorizationService.class);
		when(authorizationService.requireRole(any(FirebaseAuthenticationPrincipal.class), eq(UserRole.STAFF)))
			.thenReturn(null);
		when(stallRepository.findByStallNameIgnoreCase("Rice Bowl")).thenReturn(Optional.of(new Stall("Rice Bowl", "A. Vendor", "8:00 AM - 2:00 PM")));
		when(orderRepository.countByStallIdAndStatusIn(any(), any())).thenReturn(0L);

		StallManagementService service = new StallManagementService(stallRepository, authorizationService, orderRepository);

		assertThrows(
			DuplicateStallException.class,
			() -> service.createStall(
				new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
				new StallRequest("Rice Bowl", "B. Vendor", "9:00 AM - 3:00 PM")
			)
		);
	}
}
