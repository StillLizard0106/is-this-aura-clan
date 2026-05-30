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

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "stall_name", nullable = false, unique = true)
	private String stallName;

	@Column(name = "vendor_name", nullable = false)
	private String vendorName;

	@Column(name = "operating_hours", nullable = false)
	private String operatingHours;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected Stall() {
	}

	public Stall(String stallName, String vendorName, String operatingHours) {
		this.stallName = Objects.requireNonNull(stallName, "stallName must not be null");
		this.vendorName = Objects.requireNonNull(vendorName, "vendorName must not be null");
		this.operatingHours = Objects.requireNonNull(operatingHours, "operatingHours must not be null");
	}

	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) {
			createdAt = now;
		}
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
}
