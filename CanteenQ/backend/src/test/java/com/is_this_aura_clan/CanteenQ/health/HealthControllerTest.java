package com.is_this_aura_clan.CanteenQ.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class HealthControllerTest {

	@Test
	void healthMethodReturnsUpStatus() {
		HealthController controller = new HealthController();

		var response = controller.health();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("UP", response.getBody().status());
	}
}
