package com.felipezea.inventory.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyRequestInterceptor implements RequestInterceptor
{
    @Value("${product.auth.token.header.name}")
    private String HEADER_NAME;

    @Value("${product.auth.token}")
    private String AUTH_TOKEN;

    @Override
    public void apply(RequestTemplate template)
    {
        // Add the header key and the header value to the request template
        template.header(HEADER_NAME, AUTH_TOKEN);
    }
}
