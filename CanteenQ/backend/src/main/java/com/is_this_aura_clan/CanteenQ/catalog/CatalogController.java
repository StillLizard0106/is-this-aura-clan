package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogController {

	private final CatalogService catalogService;

	public CatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@GetMapping("/stalls")
	public ResponseEntity<List<StallResponse>> stalls() {
		return ResponseEntity.ok(catalogService.listStalls());
	}

	@GetMapping("/stalls/{stallId}/menu-items")
	public ResponseEntity<List<MenuItemResponse>> menuItems(@PathVariable UUID stallId) {
		return ResponseEntity.ok(catalogService.listMenuItems(stallId));
	}
}
