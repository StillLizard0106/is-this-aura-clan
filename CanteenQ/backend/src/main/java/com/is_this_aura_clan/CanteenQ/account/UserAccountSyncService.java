package com.is_this_aura_clan.CanteenQ.account;

import java.util.Optional;
import java.util.Set;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.InvalidFirebaseAuthorizationException;

@Service
public class UserAccountSyncService {

	private static final Set<String> DEMO_STAFF_EMAILS = Set.of(
		"staff@canteen.local",
		"staff@canteenq.local"
	);

	private final UserAccountRepository userAccountRepository;

	public UserAccountSyncService(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	@Transactional
	public UserAccount sync(FirebaseAuthenticationPrincipal principal) {
		String firebaseUid = requireText(principal.uid(), "Authenticated Firebase token did not include a uid");
		String email = requireText(principal.email(), "Authenticated Firebase token did not include an email");

		UserAccount userAccount = findExistingUser(firebaseUid, email)
			.map(existing -> updateExistingUser(existing, firebaseUid, email))
			.orElseGet(() -> createNewUser(firebaseUid, email));

		return userAccountRepository.save(userAccount);
	}

	private Optional<UserAccount> findExistingUser(String firebaseUid, String email) {
		return userAccountRepository.findByFirebaseUid(firebaseUid)
			.or(() -> userAccountRepository.findByEmail(email));
	}

	private UserAccount updateExistingUser(UserAccount userAccount, String firebaseUid, String email) {
		userAccount.updateProfile(deriveDisplayName(email), userAccount.getStudentId(), email);
		userAccount.linkFirebaseAccount(firebaseUid);
		if (isDemoStaffEmail(email)) {
			userAccount.changeRole(UserRole.STAFF);
		}
		return userAccount;
	}

	private UserAccount createNewUser(String firebaseUid, String email) {
		UserRole role = isDemoStaffEmail(email) ? UserRole.STAFF : UserRole.STUDENT;
		return new UserAccount(deriveDisplayName(email), null, email, firebaseUid, role);
	}

	private String requireText(String value, String message) {
		if (value == null || value.isBlank()) {
			throw new InvalidFirebaseAuthorizationException(message);
		}
		return value.trim();
	}

	private String deriveDisplayName(String email) {
		String localPart = email.split("@", 2)[0].trim();
		if (localPart.isBlank()) {
			return "Unknown User";
		}

		String normalized = localPart.replace('.', ' ').replace('_', ' ').trim();
		String[] parts = normalized.split("\\s+");
		StringBuilder displayName = new StringBuilder();
		for (String part : parts) {
			if (part.isBlank()) {
				continue;
			}
			if (!displayName.isEmpty()) {
				displayName.append(' ');
			}
			displayName.append(Character.toUpperCase(part.charAt(0)));
			if (part.length() > 1) {
				displayName.append(part.substring(1).toLowerCase());
			}
		}
		return displayName.isEmpty() ? "Unknown User" : displayName.toString();
	}

	private boolean isDemoStaffEmail(String email) {
		return DEMO_STAFF_EMAILS.contains(email.trim().toLowerCase(Locale.ROOT));
	}
}
