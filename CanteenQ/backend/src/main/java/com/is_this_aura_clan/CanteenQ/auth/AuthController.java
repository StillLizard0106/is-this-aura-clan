package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountSyncService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final FirebaseAuthService firebaseAuthService;
	private final UserAccountSyncService userAccountSyncService;

	public AuthController(FirebaseAuthService firebaseAuthService, UserAccountSyncService userAccountSyncService) {
		this.firebaseAuthService = firebaseAuthService;
		this.userAccountSyncService = userAccountSyncService;
	}

	@GetMapping("/verify")
	public ResponseEntity<FirebaseAuthenticationResult> verify(
		@RequestHeader(name = "Authorization", required = false) String authorizationHeader
	) {
		FirebaseAuthenticationResult authenticationResult = firebaseAuthService.authenticate(authorizationHeader);
		UserAccount userAccount = userAccountSyncService.sync(authenticationResult.principal());
		return ResponseEntity.ok(
			new FirebaseAuthenticationResult(
				authenticationResult.authenticated(),
				authenticationResult.principal(),
				authenticationResult.message(),
				userAccount == null ? null : userAccount.getRole()
			)
		);
	}
}
