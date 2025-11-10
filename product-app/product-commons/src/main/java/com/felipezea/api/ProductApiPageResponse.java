package com.felipezea.api;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ProductApiPageResponse<T>
{
    private List<ProductApiResponse<T>> data;
    private Map<String, Object> meta;

    public ProductApiPageResponse(
            List<ProductApiResponse<T>> data,
            int totalPages,
            long totalElements,
            int size,
            int number
    ) {
        this.data = data;
        this.meta = Map.of("totalPages", totalPages, "totalElements", totalElements, "size", size, "number", number);
    }

    public List<ProductApiResponse<T>> getData() {
        return data;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }
}
