package com.is_this_aura_clan.CanteenQ.secure;

import org.springframework.stereotype.Service;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class ProtectedResourceService {

	public ProtectedResourceResponse getProtectedResource(FirebaseAuthenticationPrincipal principal) {
		return new ProtectedResourceResponse(
			"Protected content accessible",
			principal.uid(),
			principal.email()
		);
	}
}
