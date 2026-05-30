package com.is_this_aura_clan.CanteenQ.order;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;
import com.is_this_aura_clan.CanteenQ.catalog.MenuItemRepository;
import com.is_this_aura_clan.CanteenQ.catalog.StallRepository;

@Service
public class OrderService {

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final UserAuthorizationService userAuthorizationService;
	private final UserAccountRepository userAccountRepository;
	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;
	private final OrderRepository orderRepository;
	private final Clock clock;
	private final OrderResponseMapper orderResponseMapper;

	public OrderService(
		UserAuthorizationService userAuthorizationService,
		UserAccountRepository userAccountRepository,
		StallRepository stallRepository,
		MenuItemRepository menuItemRepository,
		OrderRepository orderRepository,
		Clock clock,
		OrderResponseMapper orderResponseMapper
	) {
		this.userAuthorizationService = userAuthorizationService;
		this.userAccountRepository = userAccountRepository;
		this.stallRepository = stallRepository;
		this.menuItemRepository = menuItemRepository;
		this.orderRepository = orderRepository;
		this.clock = clock;
		this.orderResponseMapper = orderResponseMapper;
	}

	@Transactional
	public OrderResponse placeOrder(FirebaseAuthenticationPrincipal principal, OrderRequest request) {
		Objects.requireNonNull(principal, "principal is required");
		Objects.requireNonNull(request, "request is required");
		Objects.requireNonNull(request.stallId(), "stall id is required");
		Objects.requireNonNull(request.pickupSlot(), "pickup slot is required");
		Objects.requireNonNull(request.items(), "items are required");
		
		userAuthorizationService.requireRole(principal, UserRole.STUDENT);
		UserAccount student = userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new OrderPlacementException("No student account found for the authenticated user."));

		if (!stallRepository.existsById(request.stallId())) {
			throw new OrderPlacementException("No stall found for id " + request.stallId());
		}

		validatePickupSlot(request.pickupSlot());
		assertNoActiveOrder(student, request.stallId());

		List<OrderLine> lines = request.items().stream()
			.map(line -> resolveLine(request.stallId(), line.menuItemId(), line.quantity()))
			.toList();

		BigDecimal totalPrice = lines.stream()
			.map(OrderLine::subtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		int queueNumber = nextQueueNumber(request.stallId(), request.pickupSlot());
		CanteenOrder order = new CanteenOrder(student, request.stallId(), totalPrice, request.pickupSlot(), queueNumber);
		lines.forEach(line -> order.addItem(new OrderItem(order, line.menuItem(), line.quantity(), line.subtotal())));

		CanteenOrder savedOrder = orderRepository.save(order);
		return orderResponseMapper.toResponse(savedOrder);
	}

	private void validatePickupSlot(LocalDateTime pickupSlot) {
		LocalDateTime now = LocalDateTime.now(clock);
		if (pickupSlot.isBefore(now.plusMinutes(15))) {
			throw new OrderPlacementException("Pickup slot must be at least 15 minutes in the future.");
		}
	}

	private void assertNoActiveOrder(UserAccount student, java.util.UUID stallId) {
		if (orderRepository.existsByStudent_IdAndStallIdAndStatusIn(student.getId(), stallId, ACTIVE_STATUSES)) {
			throw new OrderConflictException("Student already has an active order for this stall.");
		}
	}

	private OrderLine resolveLine(java.util.UUID stallId, java.util.UUID menuItemId, int quantity) {
		MenuItem menuItem = menuItemRepository.findByIdAndStall_Id(menuItemId, stallId)
			.orElseThrow(() -> new OrderPlacementException("Menu item not found for id " + menuItemId));
		if (!menuItem.isAvailable()) {
			String itemName = menuItem.getItemName();
			throw new OrderPlacementException("Menu item is currently unavailable: " + (itemName == null ? menuItemId : itemName));
		}
		if (menuItem.getPrice() == null) {
			throw new OrderPlacementException("Menu item price missing for id " + menuItemId);
		}
		BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
		return new OrderLine(menuItem, quantity, subtotal);
	}

	private int nextQueueNumber(java.util.UUID stallId, LocalDateTime pickupSlot) {
		LocalDate date = pickupSlot.toLocalDate();
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = date.atTime(LocalTime.MAX);
		return orderRepository.findTopByStallIdAndPickupSlotBetweenOrderByQueueNumberDesc(stallId, start, end)
			.map(existing -> existing.getQueueNumber() + 1)
			.orElse(1);
	}

	private record OrderLine(MenuItem menuItem, int quantity, BigDecimal subtotal) {
	}
}
