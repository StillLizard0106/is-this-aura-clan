package com.is_this_aura_clan.CanteenQ.catalog;

import java.time.LocalDateTime;
import java.util.UUID;

public record StaffStallAssignmentResponse(
	UUID staffStallId,
	UUID stallId,
	String stallName,
	String vendorName,
	LocalDateTime assignedAt
) {}
