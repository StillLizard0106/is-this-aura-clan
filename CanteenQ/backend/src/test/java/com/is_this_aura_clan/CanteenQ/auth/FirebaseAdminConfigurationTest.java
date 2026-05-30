package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class FirebaseAdminConfigurationTest {

	@Test
	void failsFastWhenFirebaseAdminIsEnabledWithoutCredentialsPath() {
		new ApplicationContextRunner()
			.withUserConfiguration(FirebaseAdminConfiguration.class)
			.withPropertyValues("firebase.admin.enabled=true")
			.run(context -> {
				Throwable startupFailure = context.getStartupFailure();
				assertTrue(startupFailure != null);
				assertTrue(startupFailure.getMessage().contains("firebase.admin.credentials-path"));
			});
	}
}
