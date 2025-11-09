package com.felipezea.api;

import lombok.Data;

@Data
public class ProductApiResponse<T>
{
    public Data<T> data;

    public ProductApiResponse(Long id, T attributes)
    {
        this("product", id, attributes);
    }

    public ProductApiResponse(String type, Long id, T attributes)
    {
        this.data = new Data<>(type, id, attributes);
    }

    public record Data<T>(String type, Long id, T attributes) { }
}
