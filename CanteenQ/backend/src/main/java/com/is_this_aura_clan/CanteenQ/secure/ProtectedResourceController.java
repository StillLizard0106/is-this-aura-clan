package com.is_this_aura_clan.CanteenQ.secure;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/protected")
public class ProtectedResourceController {

	private final ProtectedResourceService protectedResourceService;

	public ProtectedResourceController(ProtectedResourceService protectedResourceService) {
		this.protectedResourceService = protectedResourceService;
	}

	@GetMapping("/profile")
	public ResponseEntity<ProtectedResourceResponse> profile(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal
	) {
		return ResponseEntity.ok(protectedResourceService.getProtectedResource(principal));
	}
}
