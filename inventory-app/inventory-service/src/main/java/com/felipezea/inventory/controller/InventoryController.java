package com.felipezea.inventory.controller;

import api.InventoryApiResponse;
import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.ProductDTO;
import com.felipezea.inventory.client.ProductClient;
import com.felipezea.inventory.service.InventoryService;
import dto.CreateInventoryDTO;
import dto.InventoryDTO;
import dto.PurchaseDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("${apiPrefix}/inventory")
public class InventoryController
{
    private final ProductClient productClient;
    private final InventoryService inventoryService;

    @PostMapping("/")
    public ResponseEntity<InventoryApiResponse<InventoryDTO>> createInventory(@Valid @RequestBody CreateInventoryDTO createInventoryDTO)
    {
        var inventory = this.inventoryService.createInventory(createInventoryDTO);

        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchase(@Valid @RequestBody PurchaseDTO purchaseDTO)
    {
        var inventory = this.inventoryService.purchase(purchaseDTO);

        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/")
    public ProductApiResponse<ProductDTO> test()
    {
        var product = this.productClient.findProductById(1L);

        return product;
    }
}
