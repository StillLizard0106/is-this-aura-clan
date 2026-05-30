package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

	List<MenuItem> findByStall_IdOrderByItemNameAsc(UUID stallId);

	Optional<MenuItem> findByStall_IdAndItemNameIgnoreCase(UUID stallId, String itemName);

	Optional<MenuItem> findByIdAndStall_Id(UUID id, UUID stallId);
}
