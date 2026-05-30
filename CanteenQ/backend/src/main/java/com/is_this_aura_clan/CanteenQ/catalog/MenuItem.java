package com.is_this_aura_clan.CanteenQ.catalog;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stall_id", nullable = false)
	private Stall stall;

	@Column(name = "item_name", nullable = false)
	private String itemName;

	@Column(nullable = false, columnDefinition = "text")
	private String description;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	private String category;

	@Column(name = "is_available", nullable = false)
	private boolean available;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected MenuItem() {
	}

	public MenuItem(Stall stall, String itemName, String description, BigDecimal price, String category, boolean available) {
		this.stall = Objects.requireNonNull(stall, "stall must not be null");
		this.itemName = Objects.requireNonNull(itemName, "itemName must not be null");
		this.description = Objects.requireNonNull(description, "description must not be null");
		this.price = Objects.requireNonNull(price, "price must not be null");
		this.category = Objects.requireNonNull(category, "category must not be null");
		this.available = available;
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

	public Stall getStall() {
		return stall;
	}

	public String getItemName() {
		return itemName;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public boolean isAvailable() {
		return available;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void updateDetails(String itemName, String description, BigDecimal price, String category) {
		this.itemName = Objects.requireNonNull(itemName, "itemName must not be null");
		this.description = Objects.requireNonNull(description, "description must not be null");
		this.price = Objects.requireNonNull(price, "price must not be null");
		this.category = Objects.requireNonNull(category, "category must not be null");
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
}
