package com.is_this_aura_clan.CanteenQ.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<CanteenOrder, UUID> {

	boolean existsByStudent_IdAndStallIdAndStatusIn(UUID studentId, UUID stallId, java.util.Collection<OrderStatus> statuses);

	long countByStallIdAndPickupSlotBetween(UUID stallId, LocalDateTime start, LocalDateTime end);

	long countByStatus(OrderStatus status);

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long countByStatusAndUpdatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);

	List<CanteenOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	Optional<CanteenOrder> findTopByStallIdAndPickupSlotBetweenOrderByQueueNumberDesc(UUID stallId, LocalDateTime start, LocalDateTime end);

	@EntityGraph(attributePaths = {"items", "items.menuItem"})
	List<CanteenOrder> findByStudent_IdOrderByCreatedAtDesc(UUID studentId);

	@EntityGraph(attributePaths = {"items", "items.menuItem", "student"})
	Optional<CanteenOrder> findByIdAndStudent_Id(UUID id, UUID studentId);

	@EntityGraph(attributePaths = {"items", "items.menuItem", "student"})
	Optional<CanteenOrder> findById(UUID id);

	@EntityGraph(attributePaths = {"items", "items.menuItem", "student"})
	List<CanteenOrder> findByStatusAndPickupSlotLessThanEqualOrderByPickupSlotAscQueueNumberAsc(OrderStatus status, LocalDateTime pickupSlot);

	@EntityGraph(attributePaths = {"items", "items.menuItem", "student"})
	List<CanteenOrder> findByStallIdAndPickupSlotBetweenAndStatusInOrderByPickupSlotAscQueueNumberAsc(
		UUID stallId,
		LocalDateTime start,
		LocalDateTime end,
		java.util.Collection<OrderStatus> statuses
	);

	@EntityGraph(attributePaths = {"items", "items.menuItem", "student"})
	List<CanteenOrder> findByStallIdAndStatusInOrderByPickupSlotAscQueueNumberAsc(UUID stallId, java.util.Collection<OrderStatus> statuses);
}
