package api;

public record JsonApiErrorDetail(
        String status,
        String title,
        String detail
) {}
