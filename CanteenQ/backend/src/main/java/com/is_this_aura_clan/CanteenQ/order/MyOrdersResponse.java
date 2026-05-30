package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;

public record MyOrdersResponse(List<OrderResponse> activeOrders, List<OrderResponse> history) {
}
