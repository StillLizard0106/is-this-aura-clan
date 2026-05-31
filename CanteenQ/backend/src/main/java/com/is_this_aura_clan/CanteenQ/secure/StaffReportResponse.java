package com.is_this_aura_clan.CanteenQ.secure;

import java.math.BigDecimal;
import java.util.List;

public record StaffReportResponse(
	long totalStalls,
	long totalOrders,
	BigDecimal totalRevenue,
	long ordersToday,
	long activeOrders,
	long pendingOrders,
	long preparingOrders,
	long readyOrders,
	long completedToday,
	long cancelledToday,
	long unclaimedToday,
	List<StaffStallReportResponse> stallBreakdowns
) {
}
