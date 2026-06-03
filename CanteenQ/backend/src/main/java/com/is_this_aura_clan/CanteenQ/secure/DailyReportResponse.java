package com.is_this_aura_clan.CanteenQ.secure;

import java.math.BigDecimal;

public record DailyReportResponse(
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
    long unclaimedToday
) {}
