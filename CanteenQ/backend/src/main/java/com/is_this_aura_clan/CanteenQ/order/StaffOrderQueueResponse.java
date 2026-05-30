package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.util.UUID;

public record StaffOrderQueueResponse(UUID stallId, List<StaffOrderResponse> orders) {
}
