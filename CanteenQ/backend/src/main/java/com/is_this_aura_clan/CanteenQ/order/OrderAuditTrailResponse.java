package com.is_this_aura_clan.CanteenQ.order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderAuditTrailResponse(
	UUID id,
	UUID orderId,
	String previousStatus,
	String newStatus,
	String changedBy,
	LocalDateTime changedAt
) {
}
