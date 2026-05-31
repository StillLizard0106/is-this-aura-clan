package com.is_this_aura_clan.CanteenQ.catalog;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "stalls")
public class Stall {

	private static final int DEFAULT_QUEUE_LIMIT = 100;
	private static final int MAX_QUEUE_LIMIT = 100;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "stall_name", nullable = false, unique = true)
	private String stallName;

	@Column(name = "vendor_name", nullable = false)
	private String vendorName;

	@Column(name = "operating_hours", nullable = false)
	private String operatingHours;

	@Column(name = "queue_limit")
	private Integer queueLimit;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected Stall() {
	}

	public Stall(String stallName, String vendorName, String operatingHours) {
		this(stallName, vendorName, operatingHours, DEFAULT_QUEUE_LIMIT);
	}

	public Stall(String stallName, String vendorName, String operatingHours, Integer queueLimit) {
		this.stallName = Objects.requireNonNull(stallName, "stallName must not be null");
		this.vendorName = Objects.requireNonNull(vendorName, "vendorName must not be null");
		this.operatingHours = Objects.requireNonNull(operatingHours, "operatingHours must not be null");
		this.queueLimit = normalizeQueueLimit(queueLimit);
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = now;
		}
		queueLimit = normalizeQueueLimit(queueLimit);
		updatedAt = now;
	}

	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public String getStallName() {
		return stallName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public String getOperatingHours() {
		return operatingHours;
	}

	public int getQueueLimit() {
		return normalizeQueueLimit(queueLimit);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void updateDetails(String stallName, String vendorName, String operatingHours) {
		this.stallName = Objects.requireNonNull(stallName, "stallName must not be null");
		this.vendorName = Objects.requireNonNull(vendorName, "vendorName must not be null");
		this.operatingHours = Objects.requireNonNull(operatingHours, "operatingHours must not be null");
	}

	private int normalizeQueueLimit(Integer value) {
		if (value == null || value <= 0) {
			return DEFAULT_QUEUE_LIMIT;
		}
		return Math.min(value, MAX_QUEUE_LIMIT);
	}
}
