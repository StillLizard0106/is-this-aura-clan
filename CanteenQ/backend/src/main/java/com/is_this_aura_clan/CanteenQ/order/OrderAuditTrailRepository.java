package com.is_this_aura_clan.CanteenQ.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAuditTrailRepository extends JpaRepository<OrderAuditTrail, UUID> {
	List<OrderAuditTrail> findByOrderIdOrderByChangedAtDesc(UUID orderId);
}
