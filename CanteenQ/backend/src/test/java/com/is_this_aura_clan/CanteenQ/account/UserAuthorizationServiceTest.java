package com.is_this_aura_clan.CanteenQ.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

class UserAuthorizationServiceTest {

	@Test
	void requireRoleReturnsStaffAccountWhenRoleMatches() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		UserAccount staffAccount = new UserAccount("Staff User", null, "staff@school.edu", "uid-staff", UserRole.STAFF);
		when(repository.findByFirebaseUid("uid-staff")).thenReturn(Optional.of(staffAccount));

		UserAuthorizationService service = new UserAuthorizationService(repository);

		UserAccount resolved = service.requireRole(
			new FirebaseAuthenticationPrincipal("uid-staff", "staff@school.edu"),
			UserRole.STAFF
		);

		assertEquals(UserRole.STAFF, resolved.getRole());
		assertEquals("staff@school.edu", resolved.getEmail());
	}

	@Test
	void requireRoleTreatsAdminAsCompatibleWithStaffAccess() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		UserAccount adminAccount = new UserAccount("Admin User", null, "admin@school.edu", "uid-admin", UserRole.ADMIN);
		when(repository.findByFirebaseUid("uid-admin")).thenReturn(Optional.of(adminAccount));

		UserAuthorizationService service = new UserAuthorizationService(repository);

		UserAccount resolved = service.requireRole(
			new FirebaseAuthenticationPrincipal("uid-admin", "admin@school.edu"),
			UserRole.STAFF
		);

		assertEquals(UserRole.ADMIN, resolved.getRole());
		assertEquals("admin@school.edu", resolved.getEmail());
	}

	@Test
	void requireRoleRejectsStudentWhenStaffRoleIsRequired() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		UserAccount studentAccount = new UserAccount("Student User", null, "student@school.edu", "uid-student", UserRole.STUDENT);
		when(repository.findByFirebaseUid("uid-student")).thenReturn(Optional.of(studentAccount));

		UserAuthorizationService service = new UserAuthorizationService(repository);

		assertThrows(
			InsufficientRoleException.class,
			() -> service.requireRole(new FirebaseAuthenticationPrincipal("uid-student", "student@school.edu"), UserRole.STAFF)
		);
	}
}
