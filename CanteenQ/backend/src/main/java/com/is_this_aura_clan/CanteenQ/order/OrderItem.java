package com.is_this_aura_clan.CanteenQ.order;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.is_this_aura_clan.CanteenQ.catalog.MenuItem;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private CanteenOrder order;

	@ManyToOne(optional = false)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItem menuItem;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal;

	protected OrderItem() {
	}

	public OrderItem(CanteenOrder order, MenuItem menuItem, int quantity, BigDecimal subtotal) {
		this.order = order;
		this.menuItem = menuItem;
		this.quantity = quantity;
		this.subtotal = subtotal;
	}

	public UUID getId() {
		return id;
	}

	public CanteenOrder getOrder() {
		return order;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}
}
