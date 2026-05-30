package com.is_this_aura_clan.CanteenQ.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

	@GetMapping
	public ResponseEntity<HealthResponse> health() {
		return ResponseEntity.ok(new HealthResponse("UP"));
	}
}
