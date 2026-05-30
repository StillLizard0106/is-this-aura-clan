package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

public record OrderStatusNotification(UUID orderId, OrderStatus status, String message) {
}
