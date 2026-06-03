package com.is_this_aura_clan.CanteenQ.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    boolean existsByMenuItem_Id(UUID menuItemId);
}
