package com.is_this_aura_clan.CanteenQ.bootstrap;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItemRepository;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;
import com.is_this_aura_clan.CanteenQ.catalog.StaffStallRepository;

class DemoDataBootstrapRunnerTest {

	@Test
	void runSeedsDemoDataWhenRepositoriesAreEmpty() {
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		StaffStallRepository staffStallRepository = mock(StaffStallRepository.class);

		when(userAccountRepository.findByFirebaseUid("demo-staff-uid")).thenReturn(Optional.empty());
		when(userAccountRepository.findByEmail("staff@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.findByFirebaseUid("demo-admin-uid")).thenReturn(Optional.empty());
		when(userAccountRepository.findByEmail("admin@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.findAllByRole(UserRole.STAFF)).thenReturn(java.util.List.of());
		when(userAccountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(stallRepository.findByStallNameIgnoreCase("Rice Bowl")).thenReturn(Optional.empty());
		when(stallRepository.save(any(Stall.class))).thenAnswer(invocation -> {
			Stall stall = invocation.getArgument(0);
			assignStallId(stall, UUID.fromString("11111111-1111-1111-1111-111111111111"));
			return stall;
		});
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(any(), any())).thenReturn(Optional.empty());
		when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(staffStallRepository.existsByStaffAndStall(any(), any())).thenReturn(false);
		when(staffStallRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		DemoDataBootstrapRunner runner = new DemoDataBootstrapRunner(userAccountRepository, stallRepository, menuItemRepository, staffStallRepository);
		runner.run();

		verify(userAccountRepository, times(2)).save(any());
		verify(stallRepository).save(any(Stall.class));
		verify(menuItemRepository, times(2)).save(any(MenuItem.class));
	}

	@Test
	void runSkipsExistingSeedData() {
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		StaffStallRepository staffStallRepository = mock(StaffStallRepository.class);

		Stall existingStall = new Stall("Rice Bowl", "Demo Vendor", "8:00 AM - 2:00 PM");
		assignStallId(existingStall, UUID.fromString("11111111-1111-1111-1111-111111111111"));

		when(userAccountRepository.findByFirebaseUid("demo-staff-uid")).thenReturn(Optional.of(new com.is_this_aura_clan.CanteenQ.account.UserAccount("Demo Staff", null, "staff@canteen.local", "demo-staff-uid", com.is_this_aura_clan.CanteenQ.account.UserRole.STAFF)));
		when(userAccountRepository.findByEmail("staff@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.findByFirebaseUid("demo-admin-uid")).thenReturn(Optional.of(new com.is_this_aura_clan.CanteenQ.account.UserAccount("Demo Admin", null, "admin@canteen.local", "demo-admin-uid", com.is_this_aura_clan.CanteenQ.account.UserRole.ADMIN)));
		when(userAccountRepository.findByEmail("admin@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.findAllByRole(UserRole.STAFF)).thenReturn(java.util.List.of());
		when(stallRepository.findByStallNameIgnoreCase("Rice Bowl")).thenReturn(Optional.of(existingStall));
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(existingStall.getId(), "Chicken Rice")).thenReturn(Optional.of(new MenuItem(existingStall, "Chicken Rice", "Rice with chicken", java.math.BigDecimal.valueOf(45), "Meals", true)));
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(existingStall.getId(), "BBQ Rice")).thenReturn(Optional.of(new MenuItem(existingStall, "BBQ Rice", "Rice with barbecue", java.math.BigDecimal.valueOf(50), "Meals", true)));
		when(staffStallRepository.existsByStaffAndStall(any(), any())).thenReturn(true);

		DemoDataBootstrapRunner runner = new DemoDataBootstrapRunner(userAccountRepository, stallRepository, menuItemRepository, staffStallRepository);
		runner.run();

		verify(userAccountRepository, never()).save(any());
		verify(stallRepository, never()).save(any(Stall.class));
		verify(menuItemRepository, never()).save(any(MenuItem.class));
	}

	@Test
	void runRepairsLegacyDemoRoles() {
		UserAccountRepository userAccountRepository = mock(UserAccountRepository.class);
		StallRepository stallRepository = mock(StallRepository.class);
		MenuItemRepository menuItemRepository = mock(MenuItemRepository.class);
		StaffStallRepository staffStallRepository = mock(StaffStallRepository.class);

		UserAccount legacyStaff = new UserAccount("Legacy Staff", null, "staff@canteen.local", "demo-staff-uid", UserRole.ADMIN);
		UserAccount legacyAdmin = new UserAccount("Legacy Admin", null, "admin@canteen.local", "demo-admin-uid", UserRole.STAFF);

		when(userAccountRepository.findByFirebaseUid("demo-staff-uid")).thenReturn(Optional.of(legacyStaff));
		when(userAccountRepository.findByEmail("staff@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.findByFirebaseUid("demo-admin-uid")).thenReturn(Optional.of(legacyAdmin));
		when(userAccountRepository.findByEmail("admin@canteen.local")).thenReturn(Optional.empty());
		when(userAccountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(stallRepository.findByStallNameIgnoreCase("Rice Bowl")).thenReturn(Optional.empty());
		when(stallRepository.save(any(Stall.class))).thenAnswer(invocation -> {
			Stall stall = invocation.getArgument(0);
			assignStallId(stall, UUID.fromString("11111111-1111-1111-1111-111111111111"));
			return stall;
		});
		when(menuItemRepository.findByStall_IdAndItemNameIgnoreCase(any(), any())).thenReturn(Optional.empty());
		when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(staffStallRepository.existsByStaffAndStall(any(), any())).thenReturn(false);
		when(staffStallRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		DemoDataBootstrapRunner runner = new DemoDataBootstrapRunner(userAccountRepository, stallRepository, menuItemRepository, staffStallRepository);
		runner.run();

		verify(userAccountRepository, times(2)).save(any());
		verify(stallRepository).save(any(Stall.class));
		verify(menuItemRepository, times(2)).save(any(MenuItem.class));
		verify(staffStallRepository).save(any());
		assertEquals(UserRole.STAFF, legacyStaff.getRole());
		assertEquals(UserRole.ADMIN, legacyAdmin.getRole());
	}

	private void assignStallId(Stall stall, UUID stallId) {
		try {
			java.lang.reflect.Field idField = Stall.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(stall, stallId);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}
}
