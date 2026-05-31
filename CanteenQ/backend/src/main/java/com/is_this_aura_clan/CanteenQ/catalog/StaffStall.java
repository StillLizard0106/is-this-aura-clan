package com.is_this_aura_clan.CanteenQ.catalog;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;

@Entity
@Table(name = "staff_stalls", uniqueConstraints = {
	@UniqueConstraint(name = "uk_staff_stall", columnNames = {"staff_id", "stall_id"})
})
public class StaffStall {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "staff_id", nullable = false)
	private UserAccount staff;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stall_id", nullable = false)
	private Stall stall;

	@Column(name = "assigned_at", nullable = false, updatable = false)
	private LocalDateTime assignedAt;

	protected StaffStall() {
	}

	public StaffStall(UserAccount staff, Stall stall) {
		this.staff = Objects.requireNonNull(staff, "staff must not be null");
		this.stall = Objects.requireNonNull(stall, "stall must not be null");
	}

	@PrePersist
	void prePersist() {
		if (assignedAt == null) {
			assignedAt = LocalDateTime.now();
		}
	}

	public UUID getId() {
		return id;
	}

	public UserAccount getStaff() {
		return staff;
	}

	public Stall getStall() {
		return stall;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}
}
