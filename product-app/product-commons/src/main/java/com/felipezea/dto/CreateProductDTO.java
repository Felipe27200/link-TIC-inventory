package com.felipezea.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {
    @NotBlank(message = "The name is required")
    String name;
    @NotBlank(message = "The description is required")
    String description;
    @NotNull(message = "The price is required")
    @Min(value = 1, message = "The price must be greater than zero")
    BigDecimal price;

}
