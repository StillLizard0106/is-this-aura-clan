package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class MenuItemManagementService {

	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;
	private final UserAuthorizationService userAuthorizationService;

	public MenuItemManagementService(
		StallRepository stallRepository,
		MenuItemRepository menuItemRepository,
		UserAuthorizationService userAuthorizationService
	) {
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
		this.userAuthorizationService = userAuthorizationService;
	}

	@Transactional(readOnly = true)
	public List<MenuItemResponse> listMenuItems(FirebaseAuthenticationPrincipal principal, UUID stallId) {
		requireStaff(principal);
		requireStall(stallId);
		return menuItemRepository.findByStall_IdOrderByItemNameAsc(stallId).stream().map(this::toResponse).toList();
	}

	@Transactional
	public MenuItemResponse createMenuItem(FirebaseAuthenticationPrincipal principal, UUID stallId, MenuItemRequest request) {
		requireStaff(principal);
		Stall stall = requireStall(stallId);
		assertItemNameAvailable(stallId, request.itemName(), null);

		MenuItem savedMenuItem = menuItemRepository.save(
			new MenuItem(stall, request.itemName(), request.description(), request.price(), request.category(), request.available())
		);
		return toResponse(savedMenuItem);
	}

	@Transactional
	public MenuItemResponse updateMenuItem(
		FirebaseAuthenticationPrincipal principal,
		UUID stallId,
		UUID menuItemId,
		MenuItemRequest request
	) {
		requireStaff(principal);
		requireStall(stallId);
		MenuItem menuItem = menuItemRepository.findByIdAndStall_Id(menuItemId, stallId)
			.orElseThrow(() -> new MenuItemNotFoundException("No menu item found for id " + menuItemId));
		assertItemNameAvailable(stallId, request.itemName(), menuItemId);

		menuItem.updateDetails(request.itemName(), request.description(), request.price(), request.category());
		menuItem.setAvailable(request.available());
		return toResponse(menuItemRepository.save(menuItem));
	}

	@Transactional
	public void deleteMenuItem(FirebaseAuthenticationPrincipal principal, UUID stallId, UUID menuItemId) {
		requireStaff(principal);
		requireStall(stallId);
		MenuItem menuItem = menuItemRepository.findByIdAndStall_Id(menuItemId, stallId)
			.orElseThrow(() -> new MenuItemNotFoundException("No menu item found for id " + menuItemId));
		menuItemRepository.delete(menuItem);
	}

	private Stall requireStall(UUID stallId) {
		return stallRepository.findById(stallId)
			.orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));
	}

	private void assertItemNameAvailable(UUID stallId, String itemName, UUID menuItemId) {
		menuItemRepository.findByStall_IdAndItemNameIgnoreCase(stallId, itemName)
			.filter(existing -> menuItemId == null || !existing.getId().equals(menuItemId))
			.ifPresent(existing -> {
				throw new DuplicateMenuItemException("Menu item already exists: " + existing.getItemName());
			});
	}

	private void requireStaff(FirebaseAuthenticationPrincipal principal) {
		userAuthorizationService.requireRole(principal, UserRole.STAFF);
	}

	private MenuItemResponse toResponse(MenuItem menuItem) {
		return new MenuItemResponse(
			menuItem.getId(),
			menuItem.getItemName(),
			menuItem.getDescription(),
			menuItem.getPrice(),
			menuItem.getCategory(),
			menuItem.isAvailable()
		);
	}
}
