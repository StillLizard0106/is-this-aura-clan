package com.is_this_aura_clan.CanteenQ.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAdminProperties;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.InvalidFirebaseAuthorizationException;

class UserAccountSyncServiceTest {

	@Test
	void syncCreatesNewStudentAccountWhenNoUserExists() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		when(repository.findByFirebaseUid("uid-123")).thenReturn(Optional.empty());
		when(repository.findByEmail("jane.doe@school.edu")).thenReturn(Optional.empty());
		when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserAccountSyncService service = new UserAccountSyncService(repository);

		UserAccount saved = service.sync(new FirebaseAuthenticationPrincipal("uid-123", "jane.doe@school.edu"));

		assertEquals("Jane Doe", saved.getName());
		assertNull(saved.getStudentId());
		assertEquals("jane.doe@school.edu", saved.getEmail());
		assertEquals("uid-123", saved.getFirebaseUid());
		assertEquals(UserRole.STUDENT, saved.getRole());
	}

	@Test
	void syncUpdatesExistingUserMatchedByFirebaseUid() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		UserAccount existing = new UserAccount("Old Name", "2024-0001", "old@school.edu", "uid-123", UserRole.STUDENT);
		when(repository.findByFirebaseUid("uid-123")).thenReturn(Optional.of(existing));
		when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserAccountSyncService service = new UserAccountSyncService(repository);

		UserAccount saved = service.sync(new FirebaseAuthenticationPrincipal("uid-123", "jane.doe@school.edu"));

		assertEquals("Jane Doe", saved.getName());
		assertEquals("2024-0001", saved.getStudentId());
		assertEquals("jane.doe@school.edu", saved.getEmail());
		assertEquals("uid-123", saved.getFirebaseUid());
	}

	@Test
	void syncPromotesDemoStaffEmailToStaffRole() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		when(repository.findByFirebaseUid("uid-staff")).thenReturn(Optional.empty());
		when(repository.findByEmail("staff@canteen.local")).thenReturn(Optional.empty());
		when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserAccountSyncService service = new UserAccountSyncService(repository);

		UserAccount saved = service.sync(new FirebaseAuthenticationPrincipal("uid-staff", "staff@canteen.local"));

		assertEquals("Staff", saved.getName());
		assertEquals("staff@canteen.local", saved.getEmail());
		assertEquals("uid-staff", saved.getFirebaseUid());
		assertEquals(UserRole.STAFF, saved.getRole());
	}

	@Test
	void syncPromotesDemoAdminEmailToAdminRole() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		when(repository.findByFirebaseUid("uid-admin")).thenReturn(Optional.empty());
		when(repository.findByEmail("admin@canteen.local")).thenReturn(Optional.empty());
		when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserAccountSyncService service = new UserAccountSyncService(repository);

		UserAccount saved = service.sync(new FirebaseAuthenticationPrincipal("uid-admin", "admin@canteen.local"));

		assertEquals("Admin", saved.getName());
		assertEquals("admin@canteen.local", saved.getEmail());
		assertEquals("uid-admin", saved.getFirebaseUid());
		assertEquals(UserRole.ADMIN, saved.getRole());
	}

	@Test
	void syncRejectsStudentEmailWhenAllowedDomainDoesNotMatch() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		when(repository.findByFirebaseUid("uid-123")).thenReturn(Optional.empty());
		when(repository.findByEmail("jane.doe@school.edu")).thenReturn(Optional.empty());

		FirebaseAdminProperties properties = new FirebaseAdminProperties();
		properties.setAllowedEmailDomain("other.school.edu");

		UserAccountSyncService service = new UserAccountSyncService(repository);
		service.setFirebaseAdminProperties(properties);

		assertThrows(InvalidFirebaseAuthorizationException.class, () ->
			service.sync(new FirebaseAuthenticationPrincipal("uid-123", "jane.doe@school.edu"))
		);
	}

	@Test
	void syncAllowsStudentEmailWhenAllowedDomainMatches() {
		UserAccountRepository repository = mock(UserAccountRepository.class);
		when(repository.findByFirebaseUid("uid-123")).thenReturn(Optional.empty());
		when(repository.findByEmail("jane.doe@other.school.edu")).thenReturn(Optional.empty());
		when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

		FirebaseAdminProperties properties = new FirebaseAdminProperties();
		properties.setAllowedEmailDomain("other.school.edu");

		UserAccountSyncService service = new UserAccountSyncService(repository);
		service.setFirebaseAdminProperties(properties);

		UserAccount saved = service.sync(new FirebaseAuthenticationPrincipal("uid-123", "jane.doe@other.school.edu"));

		assertEquals("Jane Doe", saved.getName());
		assertEquals("jane.doe@other.school.edu", saved.getEmail());
		assertEquals("uid-123", saved.getFirebaseUid());
		assertEquals(UserRole.STUDENT, saved.getRole());
	}
}
