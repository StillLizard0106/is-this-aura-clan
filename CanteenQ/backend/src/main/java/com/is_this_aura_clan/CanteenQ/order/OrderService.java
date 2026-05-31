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

/**
 * This handles the placement of new canteen orders by authenticated students.
 * 
 * This service class enforces real world business rules that are reuquired before an order is placed.
 * These include things like:
 * - including role verification
 * - pickup slot validation
 * - active-order
 * - conflict checks
 * - menu item availability
 * - queue number assignment.
 * 
 * Each others are placed inside a single database transaction so that processes like 
 * queue numbering and conflict checks remain consistent under real time requests. 
 * 
 * @see OrderCancellationService
 * @see OrderDetailService
 */

@Service
public class OrderService {

	/**
	 * Order statuses that are created for the purpose of preventing possible duplicate orders. 
	 * A student may not may not place a second order at the same time while the while any if
	 * the following statuses are in effect. 
	 */

	private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY);

	private final UserAuthorizationService userAuthorizationService;
	private final UserAccountRepository userAccountRepository;
	private final StallRepository stallRepository;
	private final MenuItemRepository menuItemRepository;
	private final OrderRepository orderRepository;
	private final Clock clock;
	private final OrderResponseMapper orderResponseMapper;

	/**
	 * A Clock is injected rather than using LocalDateTime.now() directly 
	 * so that the test can control the current time without relying on 
	 * the system clock.
	 * 
	 * @param userAuthorizationService verifies that the caller has the required role
     * @param userAccountRepository    looks up the student's account record
     * @param stallRepository          checks whether the requested stall exists
     * @param menuItemRepository       fetches and validates each ordered item
     * @param orderRepository          persists the completed order
     * @param clock                    time source used for pickup slot validation
     * @param orderResponseMapper      converts the saved entity to a response DTO
	 */
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

	/**
	 * Method that places an order on behalf of the authenticaed student
	 * 
	 * The Validations are performed in order
	 * 1. The caller must have a STUDENT Role
	 * 2. The requested stall must exist
	 * 3. The pickup slot must be at least 15 minutes in the future.
	 * 4. The student must not already have an active order at the same stall.
	 * 5. Every requested menu item must belong to the stall and be available.
	 * 
	 * If all checks pass, a queue number is assined for the pick up date
	 * and the order is saved with PENDING status.
	 * 
	 * @param principal the Firebase-authenticated user making the request
     * @param request   the order details including stall, pickup slot, and items
     * @return a response DTO representing the newly created order
     * @throws OrderPlacementException if any validation fails (stall not found,
     *                                 slot too soon, item unavailable, etc.)
     * @throws OrderConflictException  if the student already has an active order
     *                                 at the requested stall
	 */
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

	/**
	 * Validates that the requested pickup slot is far away in the future. 
	 * 
	 * The minimum lead time is 15 minutes to give the staff time to prepare order
	 * before pickup.
	 * 
	 * @param pickupSlot the requested pickup date and time
     * @throws OrderPlacementException if the slot is less than 15 minutes away
	 */
	private void validatePickupSlot(LocalDateTime pickupSlot) {
		LocalDateTime now = LocalDateTime.now(clock);
		if (pickupSlot.isBefore(now.plusMinutes(15))) {
			throw new OrderPlacementException("Pickup slot must be at least 15 minutes in the future.");
		}
	}

	/**
	 * Ensures that the student does not already have an existing order in the given stall 
	 * 
	 * Orders are consistered active when they are coded as PENDING, PREPARING, READY. 
	 * Completed, cancelled, and unclaimed orders do not block new placements.
	 * 
	 * @param student the student account to check
     * @param stallId the stall the new order targets
     * @throws OrderConflictException if an active order already exists
	 */
	private void assertNoActiveOrder(UserAccount student, java.util.UUID stallId) {
		if (orderRepository.existsByStudent_IdAndStallIdAndStatusIn(student.getId(), stallId, ACTIVE_STATUSES)) {
			throw new OrderConflictException("Student already has an active order for this stall.");
		}
	}

	/**
	 * Resolves a single line item by checking and looking up the menu item and computing its subtotal. 
	 * 
	 * The item must be available in a specific stall and must not have a non-null price.
	 * These checks prevent orders from containing items that have been disabled since the student 
	 * last viewed the menu. 
	 * 
	 * @param stallId    the stall the order belongs to
     * @param menuItemId the ID of the menu item being ordered
     * @param quantity   the number of units requested
     * @return an {@link OrderLine} containing the resolved item, quantity, and subtotal
     * @throws OrderPlacementException if the item is not found, unavailable, or missing a price
	 */
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

	/**
	 * Determines the next queue number for the given stall on the pickup date.
	 * 
	 * Created cue numbers are created or categorized to a single specific stall and calendar day.
	 * The new orders are n+1 higher than the previous order created in a speicifc stall. 
	 * or {@code 1} if no orders exist yet for that slot.
	 * 
	 * @param stallId    the stall the order is being placed at
     * @param pickupSlot the requested pickup time (only the date portion is used)
     * @return the next available queue number
	 */
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
