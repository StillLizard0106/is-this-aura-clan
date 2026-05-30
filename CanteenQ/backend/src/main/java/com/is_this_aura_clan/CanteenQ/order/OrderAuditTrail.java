package com.is_this_aura_clan.CanteenQ.order;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_audit_trail")
public class OrderAuditTrail {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Enumerated(EnumType.STRING)
	@Column(name = "previous_status", nullable = false)
	private OrderStatus previousStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "new_status", nullable = false)
	private OrderStatus newStatus;

	@Column(name = "changed_by")
	private String changedBy;

	@Column(name = "changed_at", nullable = false, updatable = false)
	private LocalDateTime changedAt;

	protected OrderAuditTrail() {
	}

	public OrderAuditTrail(UUID orderId, OrderStatus previousStatus, OrderStatus newStatus, String changedBy) {
		this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
		this.previousStatus = Objects.requireNonNull(previousStatus, "previousStatus must not be null");
		this.newStatus = Objects.requireNonNull(newStatus, "newStatus must not be null");
		this.changedBy = changedBy;
	}

	@PrePersist
	void prePersist() {
		if (changedAt == null) {
			changedAt = LocalDateTime.now();
		}
	}

	public UUID getId() {
		return id;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public OrderStatus getPreviousStatus() {
		return previousStatus;
	}

	public OrderStatus getNewStatus() {
		return newStatus;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public LocalDateTime getChangedAt() {
		return changedAt;
	}
}
