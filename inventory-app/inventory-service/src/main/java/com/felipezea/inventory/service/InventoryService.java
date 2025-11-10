package com.felipezea.inventory.service;

import api.InventoryApiResponse;
import com.felipezea.inventory.client.ProductClient;
import com.felipezea.inventory.entity.Inventory;
import com.felipezea.inventory.repository.InventoryRepository;
import dto.CreateInventoryDTO;
import dto.InventoryDTO;
import dto.PurchaseDTO;
import exception.EntityDuplicateException;
import exception.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class InventoryService
{
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final ProductClient productClient;

    public InventoryApiResponse<InventoryDTO> createInventory(CreateInventoryDTO createInventoryDTO)
    {
        log.debug("[createInventory] Create Inventory Request");

        var inventory = this.modelMapper.map(createInventoryDTO, Inventory.class);
        inventory.setProductFK(createInventoryDTO.getProductFK());

        this.inventoryRepository.findInventoryByProductFK(inventory.getProductFK())
                .ifPresent(inventoryFound -> {
                    throw new EntityDuplicateException("Inventory already exists");
                });

        var product = this.productClient.findProductById(inventory.getProductFK());

        log.info("[createInventory] product with id found: {}", product);

        inventory = this.inventoryRepository.save(inventory);

        return new InventoryApiResponse<>(
                inventory.getId(),
                this.modelMapper.map(inventory, InventoryDTO.class),
                product
        );
    }

    public Map<String, Object> purchase(PurchaseDTO purchaseDTO)
    {
        var product = this.productClient.findProductById(purchaseDTO.productFK());
        var inventory = this.inventoryRepository
                .findInventoryByProductFK(purchaseDTO.productFK())
                .orElseThrow(() -> {
                    log.error("[purchase] inventory for the product id not found: {}", product);

                    return new EntityNotFoundException("Inventory not found for the product id " + product.getData().id());
                });

        var purchaseQuantity = purchaseDTO.quantity();

        if (inventory.getQuantity() < purchaseQuantity)
        {
            throw new IllegalArgumentException(String.format("Inventory quantity (%d) less than purchase quantity (%d)",
                    inventory.getQuantity(), purchaseQuantity));
        }

        var newQuantity = inventory.getQuantity() - purchaseQuantity;

        inventory.setQuantity(newQuantity);

        inventory = inventoryRepository.save(inventory);

        log.info("[purchase] Purchase registered for product: {}, purchase amount: {}, new stock: {}",
                inventory.getProductFK(), purchaseQuantity, newQuantity);

        var purchase = Map.of(
                "data", Map.of(
                        "type", "purchase",
                        "purchaseQuantity", purchaseQuantity,
                        "currentStock", inventory.getQuantity(),
                        "totalPurchase", product.data.attributes().getPrice().multiply(BigDecimal.valueOf(purchaseQuantity))
                ),
                "relationships", product.data.attributes()
        );

        return purchase;
    }
}
