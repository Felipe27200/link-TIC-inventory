package com.felipezea.inventory.service;

import api.InventoryApiResponse;
import com.felipezea.inventory.client.ProductClient;
import com.felipezea.inventory.entity.Inventory;
import com.felipezea.inventory.repository.InventoryRepository;
import dto.CreateInventoryDTO;
import dto.InventoryDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
        var inventory = this.modelMapper.map(createInventoryDTO, Inventory.class);
        inventory.setProductFK(createInventoryDTO.getProductFK());

        var product = this.productClient.findProductById(inventory.getProductFK());

        inventory = this.inventoryRepository.save(inventory);

        return new InventoryApiResponse<>(
                inventory.getId(),
                this.modelMapper.map(inventory, InventoryDTO.class),
                product
        );
    }
}
