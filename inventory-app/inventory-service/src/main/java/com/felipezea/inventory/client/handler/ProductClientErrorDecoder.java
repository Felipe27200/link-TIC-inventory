package com.felipezea.inventory.client.handler;

import api.JsonApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.EntityDuplicateException;
import exception.EntityNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class ProductClientErrorDecoder implements ErrorDecoder
{
    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response)
    {

        String responseBody = "No details available from remote service.";

        try {
            if (response.body() != null) {
                // Read the body content for parsing
                responseBody = new String(response.body().asInputStream().readAllBytes());
            }

            // 1. Parse the JSON body into the shared DTO
            JsonApiErrorResponse errorResponse = objectMapper.readValue(responseBody, JsonApiErrorResponse.class);
            String detailMessage = extractDetailMessage(errorResponse);

            // 2. Map HTTP Status to Shared Exception
            switch (response.status()) {
                case 404:
                    return new EntityNotFoundException(detailMessage);

                case 400:
                    // This handles EntityDuplicate, TypeMismatch, Validation, etc., all translated to 400
                    return new EntityDuplicateException(detailMessage);

                default:
                    // Delegate other errors (5xx) to Feign's default handling
                    return defaultDecoder.decode(methodKey, response);
            }

        }
        catch (IOException e)
        {
            // Error reading body or JSON parsing failure
            return new RuntimeException(responseBody, e);
        }
    }

    /**
     * Extracts a unified error message string from the JSON:API response.
     * Handles both single errors and multiple validation errors.
     */
    private String extractDetailMessage(JsonApiErrorResponse response) {
        if (response.errors() == null || response.errors().isEmpty()) {
            return "Unknown error from remote service.";
        }

        // If there is only one error, return its detail directly
        if (response.errors().size() == 1) {
            return response.errors().get(0).detail();
        }

        // For multiple errors (like validation), concatenate them
        return response.errors().stream()
                .map(e -> e.detail() != null ? e.detail() : "Validation Error")
                .collect(Collectors.joining("; "));
    }
}
