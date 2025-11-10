package api;

import com.felipezea.api.ProductApiResponse;
import com.felipezea.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryApiResponse<T>
{
    public InventoryData<T> data;

    public InventoryApiResponse(Long id, T attributes, ProductApiResponse<ProductDTO> relationships)
    {
        this("inventory", id, attributes, relationships);
    }

    public InventoryApiResponse(String type, Long id, T attributes, ProductApiResponse<ProductDTO> relationships)
    {
        this.data = new InventoryData<>(type, id, attributes, relationships);
    }

    public record InventoryData<T>(String type, Long id, T attributes, ProductApiResponse<ProductDTO> relationships) { }

}
