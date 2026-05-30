package com.is_this_aura_clan.CanteenQ.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.InsufficientRoleException;

@Service
public class UserAuthorizationService {

	private final UserAccountRepository userAccountRepository;

	public UserAuthorizationService(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	@Transactional(readOnly = true)
	public UserAccount requireRole(FirebaseAuthenticationPrincipal principal, UserRole requiredRole) {
		UserAccount userAccount = userAccountRepository.findByFirebaseUid(principal.uid())
			.or(() -> userAccountRepository.findByEmail(principal.email()))
			.orElseThrow(() -> new InsufficientRoleException("No registered account found for the authenticated user."));

		if (userAccount.getRole() != requiredRole) {
			throw new InsufficientRoleException("Access requires " + requiredRole + " role.");
		}

		return userAccount;
	}
}
