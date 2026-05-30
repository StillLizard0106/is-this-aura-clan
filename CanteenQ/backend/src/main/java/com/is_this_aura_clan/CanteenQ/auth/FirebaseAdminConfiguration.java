package com.is_this_aura_clan.CanteenQ.auth;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FirebaseAdminProperties.class)
@ConditionalOnProperty(prefix = "firebase.admin", name = "enabled", havingValue = "true")
public class FirebaseAdminConfiguration {

	private static final String FIREBASE_APP_NAME = "canteenq-firebase-admin";

	@Bean
	FirebaseApp firebaseApp(FirebaseAdminProperties properties) {
		FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
			.setCredentials(loadCredentials(properties));

		if (properties.getProjectId() != null && !properties.getProjectId().isBlank()) {
			optionsBuilder.setProjectId(properties.getProjectId().trim());
		}

		return getOrCreateApp(optionsBuilder.build());
	}

	@Bean
	FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
		return FirebaseAuth.getInstance(firebaseApp);
	}

	@Bean
	FirebaseTokenVerifier firebaseTokenVerifier(FirebaseAuth firebaseAuth) {
		return new FirebaseAdminTokenVerifier(firebaseAuth);
	}

	private GoogleCredentials loadCredentials(FirebaseAdminProperties properties) {
		String credentialsPath = properties.getCredentialsPath();
		if (credentialsPath == null || credentialsPath.isBlank()) {
			throw new FirebaseAuthNotConfiguredException(
				"Firebase Admin is enabled but firebase.admin.credentials-path is missing."
			);
		}

		Path path = Path.of(credentialsPath.trim());
		if (!Files.exists(path)) {
			throw new FirebaseAuthNotConfiguredException(
				"Firebase Admin credentials file was not found at " + path.toAbsolutePath()
			);
		}

		try (InputStream inputStream = Files.newInputStream(path)) {
			return GoogleCredentials.fromStream(inputStream);
		} catch (IOException exception) {
			throw new IllegalStateException(
				"Failed to load Firebase Admin credentials from " + path.toAbsolutePath(),
				exception
			);
		}
	}

	private FirebaseApp getOrCreateApp(FirebaseOptions options) {
		List<FirebaseApp> apps = FirebaseApp.getApps();
		for (FirebaseApp app : apps) {
			if (FIREBASE_APP_NAME.equals(app.getName())) {
				return app;
			}
		}
		return FirebaseApp.initializeApp(options, FIREBASE_APP_NAME);
	}
}
