package dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseDTO(
        @NotNull(message = "The product is required")
        Long productFK,
        @NotNull(message = "The product is required")
        @Min(value = 1, message = "The quantity must be greater than zero")
        Integer quantity
) {
}
