package com.is_this_aura_clan.CanteenQ.secure;

import java.math.BigDecimal;
import java.util.UUID;

public record StaffStallReportResponse(
	UUID stallId,
	String stallName,
	long orderCount,
	BigDecimal totalRevenue
) {
}
