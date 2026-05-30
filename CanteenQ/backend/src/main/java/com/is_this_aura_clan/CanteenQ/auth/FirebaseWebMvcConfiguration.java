package com.is_this_aura_clan.CanteenQ.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FirebaseWebMvcConfiguration implements WebMvcConfigurer {

	private final FirebaseAuthInterceptor firebaseAuthInterceptor;

	public FirebaseWebMvcConfiguration(FirebaseAuthInterceptor firebaseAuthInterceptor) {
		this.firebaseAuthInterceptor = firebaseAuthInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(firebaseAuthInterceptor).addPathPatterns("/api/protected/**", "/api/staff/**", "/api/orders/**");
	}
}
