package com.felipezea.product;

import com.felipezea.api.ProductApiPageResponse;
import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.CreateProductDTO;
import com.felipezea.dto.ProductDTO;
import com.felipezea.exception.EntityDuplicateException;
import com.felipezea.exception.EntityNotFoundException;
import com.felipezea.product.entity.Product;
import com.felipezea.product.repository.ProductRepository;
import com.felipezea.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceApplicationTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    private CreateProductDTO mockCreateDTO;
    private Product mockProduct;
    private ProductDTO mockProductDTO;
    private ProductApiResponse<ProductDTO> mockProductApiResponse;

    @BeforeEach
    void setUp() {
        // DTO for product creation
        mockCreateDTO = new CreateProductDTO("Test Product", "Test Description", new BigDecimal("10.00"));

        // Product entity to simulate DB
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName(mockCreateDTO.getName());
        mockProduct.setDescription(mockCreateDTO.getDescription());
        mockProduct.setPrice(mockCreateDTO.getPrice());

        // DTO to be returned in API response
        mockProductDTO = new ProductDTO(mockProduct.getName(), mockProduct.getDescription(), mockProduct.getPrice());
        mockProductApiResponse = new ProductApiResponse<>(mockProduct.getId(), mockProductDTO);
    }

    // --- createProduct Tests ---
    @Test
    @DisplayName("Should successfully create a product")
    void createProduct_Success() {
        // Arrange: simulate empty DB (no duplicate), mapping, and saving
        when(productRepository.findByName(mockCreateDTO.getName())).thenReturn(Optional.empty());
        when(modelMapper.map(mockCreateDTO, Product.class)).thenReturn(mockProduct);
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);
        when(modelMapper.map(mockProduct, ProductDTO.class)).thenReturn(mockProductDTO);

        // Act: call service
        ProductApiResponse<ProductDTO> response = productService.createProduct(mockCreateDTO);

        // Assert: verify response content
        assertNotNull(response);
        assertEquals(mockProductDTO, response.getData().attributes());
        assertEquals(mockProduct.getId(), response.getData().id());

        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    @DisplayName("Should throw EntityDuplicateException when product name already exists")
    void createProduct_ThrowsDuplicateException() {
        when(productRepository.findByName(mockCreateDTO.getName())).thenReturn(Optional.of(mockProduct));

        assertThrows(EntityDuplicateException.class, () -> productService.createProduct(mockCreateDTO));
        verify(productRepository, never()).save(any());
    }

    // --- updateProduct Tests ---
    @Test
    @DisplayName("Should successfully update a product")
    void updateProduct_Success() {
        // Arrange: simulate product exists
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.findByName(mockCreateDTO.getName())).thenReturn(Optional.of(mockProduct));
        when(modelMapper.map(mockCreateDTO, Product.class)).thenReturn(mockProduct);
        when(productRepository.save(mockProduct)).thenReturn(mockProduct);
        when(modelMapper.map(mockProduct, ProductDTO.class)).thenReturn(mockProductDTO);

        // Act
        ProductApiResponse<ProductDTO> response = productService.updateProduct(1L, mockCreateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(mockProductDTO, response.getData().attributes());
        assertEquals(mockProduct.getId(), response.getData().id());

        verify(productRepository).save(mockProduct);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existing product")
    void updateProduct_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(1L, mockCreateDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EntityDuplicateException when updating with duplicate name")
    void updateProduct_ThrowsDuplicateException() {
        Product anotherProduct = new Product();
        anotherProduct.setId(2L);
        anotherProduct.setName(mockCreateDTO.getName());

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(productRepository.findByName(mockCreateDTO.getName())).thenReturn(Optional.of(anotherProduct));

        assertThrows(EntityDuplicateException.class, () -> productService.updateProduct(1L, mockCreateDTO));
        verify(productRepository, never()).save(any());
    }

    // --- findById Tests ---
    @Test
    @DisplayName("Should find product by id")
    void findById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(modelMapper.map(mockProduct, ProductDTO.class)).thenReturn(mockProductDTO);

        ProductApiResponse<ProductDTO> response = productService.findById(1L);

        assertNotNull(response);
        assertEquals(mockProductDTO, response.getData().attributes());
        assertEquals(mockProduct.getId(), response.getData().id());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when product not found")
    void findById_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findById(1L));
    }

    // --- deleteById Tests ---
    @Test
    @DisplayName("Should delete product successfully")
    void deleteById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        String result = productService.deleteById(1L);

        assertEquals("Product with id 1 was deleted", result);
        verify(productRepository, times(1)).delete(mockProduct);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existing product")
    void deleteById_ThrowsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteById(1L));
        verify(productRepository, never()).delete(any());
    }

    // --- findProductsPage Tests ---
    @Test
    @DisplayName("Should return a paginated list of products")
    void findProductsPage_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(mockProduct);
        var page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(mockProduct, ProductDTO.class)).thenReturn(mockProductDTO);

        // Act
        ProductApiPageResponse<ProductDTO> response = productService.findProductsPage(0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getData().size());

        // Access the attributes inside ProductApiResponse's inner record
        assertEquals(mockProductDTO, response.getData().get(0).getData().attributes());

        // Verify pagination meta info
        assertEquals(1L, ((Number) response.getMeta().get("totalElements")).longValue());
    }
}
