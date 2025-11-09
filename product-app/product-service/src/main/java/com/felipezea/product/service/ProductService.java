package com.felipezea.product.service;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.CreateProductDTO;
import com.felipezea.dto.ProductDTO;
import com.felipezea.exception.EntityDuplicateException;
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

        this.productRepository.findByName(createProductDTO.getName())
                .ifPresent(productFind -> {
                    throw new EntityDuplicateException("Product with name " + createProductDTO.getName() + " already exists");
                });

        product = this.productRepository.save(product);

        log.info("[createProduct] product created: {}", product);

        return new ProductApiResponse<>(product.getId(), this.modelMapper.map(product, ProductDTO.class));
    }

    public ProductApiResponse<ProductDTO> updateProduct(Long id, CreateProductDTO createProductDTO)
    {
        log.info("[updateProduct] - start");

        var product = this.modelMapper.map(createProductDTO, Product.class);

        var oldProduct = this.findEntityById(id);
        var checkName = this.productRepository.findByName(createProductDTO.getName()).orElse(null);

        if (checkName != null && !checkName.getId().equals(oldProduct.getId()))
            throw new EntityDuplicateException("Product with name " + createProductDTO.getName() + " already exists");

        product = this.productRepository.save(product);

        log.info("[updateProduct] product updated: {}", product);

        return new ProductApiResponse<>(product.getId(), this.modelMapper.map(product, ProductDTO.class));
    }

    public ProductApiResponse<ProductDTO> findById(Long id)
    {
        log.debug("[findById] - Search product by id: {}", id);

        var product = this.findEntityById(id);

        log.debug("[findById] - product found: {}", product);

        return new ProductApiResponse<>(product.getId(), this.modelMapper.map(product, ProductDTO.class));
    }

    public String deleteById(Long id)
    {
        var product = this.findEntityById(id);

        this.productRepository.delete(product);

        log.info("[deleteById] - product deleted: {}", product);

        return "Product with id " + id + " was deleted";
    }

    private Product findEntityById(Long id)
    {
        return this.productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[findById] - Product not found with id: {}", id);

                    return new EntityNotFoundException("Product not found with id: " + id);
                });
    }
}
