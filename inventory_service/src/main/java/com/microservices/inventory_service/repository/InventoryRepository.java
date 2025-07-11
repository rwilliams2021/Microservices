package com.microservices.inventory_service.repository;

import com.microservices.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsBySkuCodeInAndQuantityGreaterThanEqual(String skuCode, Integer quantity);
}
