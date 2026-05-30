package com.is_this_aura_clan.CanteenQ.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BearerTokenExtractorTest {

	@Test
	void extractsTokenFromBearerHeader() {
		assertEquals("abc123", BearerTokenExtractor.extract("Bearer abc123"));
	}

	@Test
	void rejectsMissingHeader() {
		assertThrows(InvalidFirebaseAuthorizationException.class, () -> BearerTokenExtractor.extract(null));
	}

	@Test
	void rejectsNonBearerHeader() {
		assertThrows(InvalidFirebaseAuthorizationException.class, () -> BearerTokenExtractor.extract("Token abc123"));
	}
}
