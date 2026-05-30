package com.is_this_aura_clan.CanteenQ.catalog;

import jakarta.validation.constraints.NotBlank;

public record StallRequest(
	@NotBlank(message = "stall name is required") String stallName,
	@NotBlank(message = "vendor name is required") String vendorName,
	@NotBlank(message = "operating hours are required") String operatingHours
) {
}
