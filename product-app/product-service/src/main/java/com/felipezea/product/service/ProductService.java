package com.felipezea.product.service;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.CreateProductDTO;
import com.felipezea.dto.ProductDTO;
import com.felipezea.exception.EntityNotFoundException;
import com.felipezea.product.entity.Product;
import com.felipezea.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductApiResponse<ProductDTO> createProduct(CreateProductDTO createProductDTO)
    {
        log.info("[createProduct] - start");

        var product = this.modelMapper.map(createProductDTO, Product.class);

        product = this.productRepository.save(product);

        log.info("[createProduct] product created: {}", product);

        return new ProductApiResponse<>(product.getId(), this.modelMapper.map(product, ProductDTO.class));
    }

    public ProductApiResponse<ProductDTO> findById(Long id)
    {
        log.debug("[findById] - Search product by id: {}", id);

        var product = this.productRepository.findById(id)
            .orElseThrow(() -> {
                log.error("[findById] - Product not found with id: {}", id);

                return new EntityNotFoundException("Product not found with id: " + id);
            });

        log.debug("[findById] - product found: {}", product);

        return new ProductApiResponse<>(product.getId(), this.modelMapper.map(product, ProductDTO.class));
    }
}
