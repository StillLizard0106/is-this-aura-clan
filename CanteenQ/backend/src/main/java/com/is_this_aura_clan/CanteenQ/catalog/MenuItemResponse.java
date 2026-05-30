package com.is_this_aura_clan.CanteenQ.catalog;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(UUID id, String itemName, String description, BigDecimal price, String category, boolean available) {
}
