package com.is_this_aura_clan.CanteenQ.secure;

import java.util.List;

public record StaffReportResponse(
	long totalStalls,
	long totalOrders,
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
