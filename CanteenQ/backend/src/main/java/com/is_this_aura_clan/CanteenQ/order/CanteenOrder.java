package com.is_this_aura_clan.CanteenQ.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.catalog.Stall;

@Entity
@Table(name = "orders")
public class CanteenOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private UserAccount student;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "stall_id", nullable = false)
	private Stall stall;

	@Column(name = "total_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalPrice;

	@Column(name = "pickup_slot", nullable = false)
	private LocalDateTime pickupSlot;

	@Column(name = "queue_number", nullable = false)
	private int queueNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@OneToMany(mappedBy = "order", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected CanteenOrder() {
	}

	public CanteenOrder(UserAccount student, Stall stall, BigDecimal totalPrice, LocalDateTime pickupSlot, int queueNumber) {
		this.student = Objects.requireNonNull(student, "student must not be null");
		this.stall = Objects.requireNonNull(stall, "stall must not be null");
		this.totalPrice = Objects.requireNonNull(totalPrice, "totalPrice must not be null");
		this.pickupSlot = Objects.requireNonNull(pickupSlot, "pickupSlot must not be null");
		this.queueNumber = queueNumber;
		this.status = OrderStatus.PENDING;
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		/**
		 * Capture a single timestamp so createdAt and updatedAt are identical on insert.
		 * createdAt is guarded against re-assignment because the column is marked updatable=false,
		 * but we check anyway in case this method is ever called manually in tests.
		 */
		if (createdAt == null) {
			createdAt = now;
		}
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void addItem(OrderItem item) {
		items.add(item);
	}

	public UUID getId() {
		return id;
	}

	public UserAccount getStudent() {
		return student;
	}

	public UUID getStallId() {
		return stall.getId();
	}

	public Stall getStall() {
		return stall;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public LocalDateTime getPickupSlot() {
		return pickupSlot;
	}

	public int getQueueNumber() {
		return queueNumber;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void updateStatus(OrderStatus nextStatus) {
		Objects.requireNonNull(nextStatus, "nextStatus must not be null");
		if (status == nextStatus) {
			return;
		}
		if (!isValidTransition(status, nextStatus)) {
			throw new OrderStatusTransitionException("Cannot change order status from " + status + " to " + nextStatus + ".");
		}
		status = nextStatus;
	}

	public void cancel(LocalDateTime now) {
		Objects.requireNonNull(now, "now must not be null");
		if (status != OrderStatus.PENDING) {
			throw new OrderActionException("Only pending orders can be cancelled.");
		}
		// Students cannot cancel within 15 minutes of pickup — by that point
    	// the stall has likely already started preparing the order.
		if (now.isAfter(pickupSlot.minusMinutes(15))) {
			throw new OrderActionException("Orders can only be cancelled at least 15 minutes before pickup.");
		}
		status = OrderStatus.CANCELLED;
	}

	public void markUnclaimed(LocalDateTime now) {
		Objects.requireNonNull(now, "now must not be null");
		if (status != OrderStatus.READY) {
			throw new OrderActionException("Only ready orders can be marked as unclaimed.");
		}
		// Give students a 15-minute grace period after the pickup slot before
    	// the order is considered abandoned. This matches the cancellation window
    	// and the scheduler's cutoff in OrderUnclaimedScheduler.
		if (now.isBefore(pickupSlot.plusMinutes(15))) {
			throw new OrderActionException("Orders can only be marked unclaimed after the pickup grace period.");
		}
		status = OrderStatus.UNCLAIMED;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	private boolean isValidTransition(OrderStatus currentStatus, OrderStatus nextStatus) {
		// Only staff-driven forward transitions are handled here.
    	// CANCELLED is set via cancel(), UNCLAIMED via markUnclaimed() —
    	// both enforce their own preconditions and bypass this method.
		return switch (currentStatus) {
			case PENDING -> nextStatus == OrderStatus.PREPARING || nextStatus == OrderStatus.CANCELLED;
			case PREPARING -> nextStatus == OrderStatus.READY;
			case READY -> nextStatus == OrderStatus.COMPLETED;
			default -> false;
		};
	}
}
