package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.order.OrderRepository;
import com.is_this_aura_clan.CanteenQ.order.OrderStatus;

@Service
public class CatalogService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;
	private final OrderRepository orderRepository;

	public CatalogService(StallRepository stallRepository, MenuItemRepository menuItemRepository, OrderRepository orderRepository) {
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
		this.orderRepository = orderRepository;
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
		int queueLimit = stall.getQueueLimit();
		int queueSlotsLeft = Math.max(0, queueLimit - Math.toIntExact(orderRepository.countByStall_IdAndStatusIn(stall.getId(), ACTIVE_STATUSES)));
		return new StallResponse(
			stall.getId(),
			stall.getStallName(),
			stall.getVendorName(),
			stall.getOperatingHours(),
			queueLimit,
			queueSlotsLeft
		);
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
