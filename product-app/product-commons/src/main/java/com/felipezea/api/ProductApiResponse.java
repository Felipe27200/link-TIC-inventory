package com.felipezea.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductApiResponse<T>
{
    public ProductData<T> data;

    public ProductApiResponse(Long id, T attributes)
    {
        this("product", id, attributes);
    }

    public ProductApiResponse(String type, Long id, T attributes)
    {
        this.data = new ProductData<>(type, id, attributes);
    }

    public record ProductData<T>(String type, Long id, T attributes) { }
}
