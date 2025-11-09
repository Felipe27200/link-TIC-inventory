package com.felipezea.product.controller;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.CreateProductDTO;
import com.felipezea.dto.ProductDTO;
import com.felipezea.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProductApiResponse<ProductDTO>> findById(@PathVariable Long id)
    {
        var product = productService.findById(id);

        return ResponseEntity.ok(product);
    }
}
