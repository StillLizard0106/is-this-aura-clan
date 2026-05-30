package com.is_this_aura_clan.CanteenQ.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class FirebaseAuthInterceptor implements HandlerInterceptor {

	private final FirebaseAuthService firebaseAuthService;

	public FirebaseAuthInterceptor(FirebaseAuthService firebaseAuthService) {
		this.firebaseAuthService = firebaseAuthService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		FirebaseAuthenticationResult authenticationResult = firebaseAuthService.authenticate(request.getHeader("Authorization"));
		request.setAttribute(FirebaseRequestAttributes.PRINCIPAL, authenticationResult.principal());
		return true;
	}
}
