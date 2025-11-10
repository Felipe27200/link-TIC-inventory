package com.felipezea.inventory.client;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient
{
    @GetMapping("/api/products/{id}")
    ProductApiResponse<ProductDTO> findProductById(@PathVariable Long id);
}
