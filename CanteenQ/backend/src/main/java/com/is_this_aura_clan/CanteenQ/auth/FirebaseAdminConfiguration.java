package com.is_this_aura_clan.CanteenQ.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "firebase.admin", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(FirebaseAdminProperties.class)
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
		// Priority: explicit credentialsPath -> credentialsJson (raw or base64) -> GOOGLE_APPLICATION_CREDENTIALS -> Application Default Credentials
		String credentialsPath = properties.getCredentialsPath();
		String credentialsJson = properties.getCredentialsJson();

		try {
			if (credentialsPath != null && !credentialsPath.isBlank()) {
				Path path = Path.of(credentialsPath.trim());
				if (!Files.exists(path)) {
					throw new FirebaseAuthNotConfiguredException(
						"Firebase Admin credentials file was not found at " + path.toAbsolutePath()
					);
				}
				try (InputStream inputStream = Files.newInputStream(path)) {
					return GoogleCredentials.fromStream(inputStream);
				}
			}

			if (credentialsJson != null && !credentialsJson.isBlank()) {
				String trimmed = credentialsJson.trim();
				InputStream is;
				if (trimmed.startsWith("{")) {
					is = new ByteArrayInputStream(trimmed.getBytes(StandardCharsets.UTF_8));
				} else {
					byte[] decoded = Base64.getDecoder().decode(trimmed);
					is = new ByteArrayInputStream(decoded);
				}
				try (InputStream inputStream = is) {
					return GoogleCredentials.fromStream(inputStream);
				}
			}

			String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
			if (envPath != null && !envPath.isBlank()) {
				Path path = Path.of(envPath.trim());
				if (Files.exists(path)) {
					try (InputStream inputStream = Files.newInputStream(path)) {
						return GoogleCredentials.fromStream(inputStream);
					}
				}
			}

			// Fall back to application default credentials (e.g., when running on GCP)
			return GoogleCredentials.getApplicationDefault();
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to load Firebase Admin credentials", exception);
		} catch (IllegalArgumentException iae) {
			throw new FirebaseAuthNotConfiguredException("Failed to decode provided Firebase Admin credentials: " + iae.getMessage());
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
