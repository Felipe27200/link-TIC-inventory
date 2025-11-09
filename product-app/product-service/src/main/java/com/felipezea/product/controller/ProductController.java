package com.felipezea.product.controller;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.CreateProductDTO;
import com.felipezea.dto.ProductDTO;
import com.felipezea.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${apiPrefix}/products")
public class ProductController
{
    private final ProductService productService;

    @PostMapping("/")
    public ResponseEntity<ProductApiResponse<ProductDTO>> createProduct(@Valid @RequestBody CreateProductDTO productDTO)
    {
        var product = productService.createProduct(productDTO);

        return ResponseEntity.ok(product);
    }
}
