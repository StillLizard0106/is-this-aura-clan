package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;

	public CatalogService(StallRepository stallRepository, MenuItemRepository menuItemRepository) {
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
	}

	@Transactional(readOnly = true)
	public List<StallResponse> listStalls() {
		return stallRepository.findAll()
			.stream()
			.map(this::toStallResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<MenuItemResponse> listMenuItems(UUID stallId) {
		if (!stallRepository.existsById(stallId)) {
			throw new StallNotFoundException("No stall found for id " + stallId);
		}

		return menuItemRepository.findByStall_IdOrderByItemNameAsc(stallId)
			.stream()
			.map(this::toMenuItemResponse)
			.toList();
	}

	private StallResponse toStallResponse(Stall stall) {
		return new StallResponse(stall.getId(), stall.getStallName(), stall.getVendorName(), stall.getOperatingHours());
	}

	private MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
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
