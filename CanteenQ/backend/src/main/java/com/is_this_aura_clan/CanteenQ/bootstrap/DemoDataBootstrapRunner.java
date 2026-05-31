package com.is_this_aura_clan.CanteenQ.bootstrap;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItemRepository;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;
import com.is_this_aura_clan.CanteenQ.catalog.StaffStall;
import com.is_this_aura_clan.CanteenQ.catalog.StaffStallRepository;

@Component
@ConditionalOnProperty(prefix = "app.bootstrap", name = "enabled", havingValue = "true")
public class DemoDataBootstrapRunner implements CommandLineRunner {

	private static final String DEMO_STAFF_EMAIL = "staff@canteen.local";
	private static final String DEMO_STAFF_UID = "demo-staff-uid";
	private static final String DEMO_STALL_NAME = "Rice Bowl";

	private final UserAccountRepository userAccountRepository;
	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;
	private final StaffStallRepository staffStallRepository;

	public DemoDataBootstrapRunner(
		UserAccountRepository userAccountRepository,
		StallRepository stallRepository,
		MenuItemRepository menuItemRepository,
		StaffStallRepository staffStallRepository
	) {
		this.userAccountRepository = userAccountRepository;
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
		this.staffStallRepository = staffStallRepository;
	}

	@Override
	public void run(String... args) {
		UserAccount staff = seedStaffAccount();
		Stall stall = seedCatalog();
		seedStaffStallAssignment(staff, stall);
	}

	private UserAccount seedStaffAccount() {
		return userAccountRepository.findByEmail(DEMO_STAFF_EMAIL)
			.map(existing -> {
				boolean changed = false;
				if (existing.getRole() != UserRole.STAFF) {
					existing.changeRole(UserRole.STAFF);
					changed = true;
				}
				if (!DEMO_STAFF_UID.equals(existing.getFirebaseUid())) {
					existing.linkFirebaseAccount(DEMO_STAFF_UID);
					changed = true;
				}
				return changed ? userAccountRepository.save(existing) : existing;
			})
			.orElseGet(() -> userAccountRepository.save(
				new UserAccount("Demo Staff", null, DEMO_STAFF_EMAIL, DEMO_STAFF_UID, UserRole.STAFF)
			));
	}

	private Stall seedCatalog() {
		Stall stall = stallRepository.findByStallNameIgnoreCase(DEMO_STALL_NAME)
			.orElseGet(() -> stallRepository.save(new Stall(DEMO_STALL_NAME, "Demo Vendor", "8:00 AM - 2:00 PM")));

		seedMenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		seedMenuItem(stall, "BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals", true);
		
		return stall;
	}

	private void seedMenuItem(Stall stall, String itemName, String description, BigDecimal price, String category, boolean available) {
		menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stall.getId(), itemName)
			.orElseGet(() -> menuItemRepository.save(
				new MenuItem(stall, itemName, description, price, category, available)
			));
	}

	private void seedStaffStallAssignment(UserAccount staff, Stall stall) {
		if (!staffStallRepository.existsByStaffAndStall(staff, stall)) {
			staffStallRepository.save(new StaffStall(staff, stall));
		}
	}
}
