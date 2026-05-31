package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.UUID;

public record StallResponse(
	UUID id,
	String stallName,
	String vendorName,
	String operatingHours,
	int queueLimit,
	int queueSlotsLeft
) {
}
