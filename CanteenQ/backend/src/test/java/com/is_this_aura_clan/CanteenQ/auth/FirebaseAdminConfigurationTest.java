package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class FirebaseAdminConfigurationTest {

	@Test
	void failsFastWhenFirebaseAdminIsEnabledWithoutValidCredentials() {
		new ApplicationContextRunner()
			.withUserConfiguration(FirebaseAdminConfiguration.class)
			.withPropertyValues(
				"firebase.admin.enabled=true"
			)
			.run(context -> {
				Throwable startupFailure = context.getStartupFailure();
				assertTrue(startupFailure != null, "Expected startup failure when Firebase is enabled without credentials");
				String failureMessage = startupFailure.getMessage();
				assertTrue(
					failureMessage.contains("Failed to load") || failureMessage.contains("Firebase Admin") || failureMessage.contains("credentials"),
					"Expected meaningful error message about credentials, got: " + failureMessage
				);
			});
	}
}
