package com.is_this_aura_clan.CanteenQ.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UserAccountTest {

	@Test
	void constructorSetsCoreFields() {
		UserAccount user = new UserAccount(
			"Jane Doe",
			"2024-0001",
			"jane.doe@school.edu",
			"firebase-uid-1",
			UserRole.STUDENT
		);

		assertNull(user.getId());
		assertEquals("Jane Doe", user.getName());
		assertEquals("2024-0001", user.getStudentId());
		assertEquals("jane.doe@school.edu", user.getEmail());
		assertEquals("firebase-uid-1", user.getFirebaseUid());
		assertEquals(UserRole.STUDENT, user.getRole());
	}

	@Test
	void updateProfileChangesMutableFields() {
		UserAccount user = new UserAccount(
			"Jane Doe",
			"2024-0001",
			"jane.doe@school.edu",
			"firebase-uid-1",
			UserRole.STUDENT
		);

		user.updateProfile("Janet Doe", "2024-0002", "janet.doe@school.edu");
		user.linkFirebaseAccount("firebase-uid-2");
		user.changeRole(UserRole.ADMIN);

		assertEquals("Janet Doe", user.getName());
		assertEquals("2024-0002", user.getStudentId());
		assertEquals("janet.doe@school.edu", user.getEmail());
		assertEquals("firebase-uid-2", user.getFirebaseUid());
		assertEquals(UserRole.ADMIN, user.getRole());
	}
}
