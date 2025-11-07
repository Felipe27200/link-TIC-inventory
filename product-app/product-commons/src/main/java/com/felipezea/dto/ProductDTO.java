package com.felipezea.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price
) {
}
