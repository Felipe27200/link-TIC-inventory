package com.felipezea.inventory.repository;

import com.felipezea.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>
{
    Optional<Inventory> findInventoryByProductFK(Long productFK);
}
