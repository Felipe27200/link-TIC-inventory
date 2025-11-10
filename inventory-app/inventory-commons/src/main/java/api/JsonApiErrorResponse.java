package api;

import java.util.List;

public record JsonApiErrorResponse(
        List<JsonApiErrorDetail> errors
) {
}
