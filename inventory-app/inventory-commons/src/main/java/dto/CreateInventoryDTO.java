package dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInventoryDTO
{
    @NotNull(message = "The product id is required")
    private Long productFK;
    @NotNull(message = "The quantity is required")
    @Min(value = 1, message = "The quantity must be greater than zero")
    private Integer quantity;
}
