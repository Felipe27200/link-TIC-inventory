package com.felipezea.inventory;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.ProductDTO;
import com.felipezea.inventory.client.ProductClient;
import com.felipezea.inventory.entity.Inventory;
import com.felipezea.inventory.repository.InventoryRepository;
import com.felipezea.inventory.service.InventoryService;
import dto.CreateInventoryDTO;
import dto.InventoryDTO;
import dto.PurchaseDTO;
import exception.EntityDuplicateException;
import exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryService.
 * Uses Mockito to mock dependencies and JUnit 5 for assertions.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceApplicationTests {

    @InjectMocks
    private InventoryService inventoryService; // Service under test

    @Mock
    private InventoryRepository inventoryRepository; // Mocked repository

    @Mock
    private ModelMapper modelMapper; // Mocked ModelMapper

    @Mock
    private ProductClient productClient; // Mocked ProductClient

    // --- Constants for test data ---
    private final Long PRODUCT_FK = 100L; // Product foreign key
    private final int INITIAL_QUANTITY = 50; // Initial inventory quantity
    private final BigDecimal PRODUCT_PRICE = new BigDecimal("10.00"); // Product price

    // --- Mocked objects ---
    private Inventory mockInventory; // Mock Inventory entity
    private CreateInventoryDTO mockCreateDto; // Mock DTO used for creation
    private ProductApiResponse<ProductDTO> mockProductApiResponse; // Mock product response

    /**
     * Sets up common test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Setup mock inventory entity
        mockInventory = new Inventory();
        mockInventory.setId(1L);
        mockInventory.setProductFK(PRODUCT_FK);
        mockInventory.setQuantity(INITIAL_QUANTITY);

        // Setup DTO for creating inventory
        mockCreateDto = new CreateInventoryDTO();
        mockCreateDto.setProductFK(PRODUCT_FK);
        mockCreateDto.setQuantity(INITIAL_QUANTITY);

        // Setup product DTO and wrap in ProductApiResponse
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPrice(PRODUCT_PRICE);
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");

        mockProductApiResponse = new ProductApiResponse<>(PRODUCT_FK, productDTO);
    }

    // --- createInventory Tests ---

    @Test
    @DisplayName("Should successfully create a new inventory")
    void createInventory_Success() {
        // Arrange: prepare expected DTO and mock behavior
        InventoryDTO expectedDto = new InventoryDTO(PRODUCT_FK, INITIAL_QUANTITY);

        when(modelMapper.map(mockCreateDto, Inventory.class)).thenReturn(mockInventory);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.empty());
        when(inventoryRepository.save(mockInventory)).thenReturn(mockInventory);
        when(modelMapper.map(mockInventory, InventoryDTO.class)).thenReturn(expectedDto);
        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);

        // Act: call the service method
        var result = inventoryService.createInventory(mockCreateDto);

        // Assert: validate response
        assertNotNull(result);
        assertEquals(expectedDto, result.getData().attributes());  // Check the attributes inside the response
        assertEquals(mockInventory.getId(), result.getData().id()); // Verify the inventory ID
        assertEquals(mockProductApiResponse, result.getData().relationships()); // Verify relationships

        verify(inventoryRepository, times(1)).save(mockInventory); // Ensure save is called once
    }

    @Test
    @DisplayName("Should throw EntityDuplicateException when inventory already exists")
    void createInventory_ThrowsDuplicateException() {
        when(modelMapper.map(mockCreateDto, Inventory.class)).thenReturn(mockInventory);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.of(mockInventory));

        // Assert: exception is thrown and save is never called
        assertThrows(EntityDuplicateException.class, () -> inventoryService.createInventory(mockCreateDto));
        verify(inventoryRepository, never()).save(any());
    }

    // --- updateInventory Tests ---

    @Test
    @DisplayName("Should successfully update existing inventory quantity")
    void updateInventory_Success() {
        // Arrange
        int newQuantity = 99;
        CreateInventoryDTO updateDto = new CreateInventoryDTO(PRODUCT_FK, newQuantity);

        Inventory oldInventory = new Inventory();
        oldInventory.setProductFK(PRODUCT_FK);
        oldInventory.setQuantity(INITIAL_QUANTITY);

        Inventory updatedInventory = new Inventory();
        updatedInventory.setProductFK(PRODUCT_FK);
        updatedInventory.setQuantity(newQuantity);

        InventoryDTO expectedDto = new InventoryDTO(PRODUCT_FK, newQuantity);

        when(modelMapper.map(updateDto, Inventory.class)).thenReturn(updatedInventory);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.of(oldInventory));
        when(inventoryRepository.save(oldInventory)).thenReturn(updatedInventory);
        when(modelMapper.map(updatedInventory, InventoryDTO.class)).thenReturn(expectedDto);
        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);

        // Act
        var result = inventoryService.updateInventory(updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result.getData().attributes());  // Check updated attributes
        assertEquals(updatedInventory.getId(), result.getData().id()); // Check inventory ID
        assertEquals(mockProductApiResponse, result.getData().relationships()); // Check relationships

        verify(inventoryRepository).save(oldInventory); // Ensure save was called
        assertEquals(newQuantity, oldInventory.getQuantity()); // Verify quantity update
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when inventory to update is missing")
    void updateInventory_ThrowsNotFound() {
        CreateInventoryDTO updateDto = new CreateInventoryDTO(PRODUCT_FK, 99);

        when(modelMapper.map(updateDto, Inventory.class)).thenReturn(mockInventory);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.empty());
        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);

        assertThrows(EntityNotFoundException.class, () -> inventoryService.updateInventory(updateDto));
        verify(inventoryRepository, never()).save(any()); // Ensure save was never called
    }

    // --- purchase Tests ---

    @Test
    @DisplayName("Should successfully process purchase and update stock")
    void purchase_Success() {
        int purchaseAmount = 10;
        PurchaseDTO purchaseDTO = new PurchaseDTO(PRODUCT_FK, purchaseAmount);

        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.save(mockInventory)).thenReturn(mockInventory);

        // Act
        Map<String, Object> result = inventoryService.purchase(purchaseDTO);

        // Assert
        int expectedNewStock = INITIAL_QUANTITY - purchaseAmount;
        assertEquals(expectedNewStock, mockInventory.getQuantity());
        verify(inventoryRepository, times(1)).save(mockInventory);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        BigDecimal totalPurchase = (BigDecimal) data.get("totalPurchase");
        assertEquals(PRODUCT_PRICE.multiply(BigDecimal.valueOf(purchaseAmount)), totalPurchase); // Validate total
        assertEquals(expectedNewStock, data.get("currentStock")); // Validate stock
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when purchase quantity exceeds stock")
    void purchase_ThrowsInsufficientStock() {
        PurchaseDTO purchaseDTO = new PurchaseDTO(PRODUCT_FK, INITIAL_QUANTITY + 1);

        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.of(mockInventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.purchase(purchaseDTO));
        verify(inventoryRepository, never()).save(any()); // Save should not happen
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when inventory is missing for purchase")
    void purchase_ThrowsInventoryNotFound() {
        PurchaseDTO purchaseDTO = new PurchaseDTO(PRODUCT_FK, 10);

        when(productClient.findProductById(PRODUCT_FK)).thenReturn(mockProductApiResponse);
        when(inventoryRepository.findInventoryByProductFK(PRODUCT_FK)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inventoryService.purchase(purchaseDTO));
        verify(inventoryRepository, never()).save(any()); // Save should not happen
    }
}
