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

@Component
@ConditionalOnProperty(prefix = "app.bootstrap", name = "enabled", havingValue = "true")
public class DemoDataBootstrapRunner implements CommandLineRunner {

	private static final String DEMO_STAFF_EMAIL = "staff@canteenq.local";
	private static final String DEMO_STAFF_UID = "demo-staff-uid";
	private static final String DEMO_STALL_NAME = "Rice Bowl";

	private final UserAccountRepository userAccountRepository;
	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;

	public DemoDataBootstrapRunner(
		UserAccountRepository userAccountRepository,
		StallRepository stallRepository,
		MenuItemRepository menuItemRepository
	) {
		this.userAccountRepository = userAccountRepository;
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
	}

	@Override
	public void run(String... args) {
		seedStaffAccount();
		seedCatalog();
	}

	private void seedStaffAccount() {
		userAccountRepository.findByEmail(DEMO_STAFF_EMAIL)
			.orElseGet(() -> userAccountRepository.save(
				new UserAccount("Demo Staff", null, DEMO_STAFF_EMAIL, DEMO_STAFF_UID, UserRole.STAFF)
			));
	}

	private void seedCatalog() {
		Stall stall = stallRepository.findByStallNameIgnoreCase(DEMO_STALL_NAME)
			.orElseGet(() -> stallRepository.save(new Stall(DEMO_STALL_NAME, "Demo Vendor", "8:00 AM - 2:00 PM")));

		seedMenuItem(stall, "Chicken Rice", "Rice with chicken", new BigDecimal("45.00"), "Meals", true);
		seedMenuItem(stall, "BBQ Rice", "Rice with barbecue", new BigDecimal("50.00"), "Meals", true);
	}

	private void seedMenuItem(Stall stall, String itemName, String description, BigDecimal price, String category, boolean available) {
		menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stall.getId(), itemName)
			.orElseGet(() -> menuItemRepository.save(
				new MenuItem(stall, itemName, description, price, category, available)
			));
	}
}
