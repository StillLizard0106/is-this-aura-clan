package com.is_this_aura_clan.CanteenQ.account;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(name = "student_id", unique = true)
	private String studentId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "firebase_uid", nullable = false, unique = true)
	private String firebaseUid;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	protected UserAccount() {
	}

	public UserAccount(String name, String studentId, String email, String firebaseUid, UserRole role) {
		this.name = name;
		this.studentId = studentId;
		this.email = email;
		this.firebaseUid = firebaseUid;
		this.role = Objects.requireNonNull(role, "role must not be null");
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

	public String getName() {
		return name;
	}

	public String getStudentId() {
		return studentId;
	}

	public String getEmail() {
		return email;
	}

	public String getFirebaseUid() {
		return firebaseUid;
	}

	public UserRole getRole() {
		return role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void updateProfile(String name, String studentId, String email) {
		this.name = name;
		this.studentId = studentId;
		this.email = email;
	}

	public void linkFirebaseAccount(String firebaseUid) {
		this.firebaseUid = firebaseUid;
	}

	public void changeRole(UserRole role) {
		this.role = Objects.requireNonNull(role, "role must not be null");
	}
}
