package com.felipezea.inventory.configuration;

import com.felipezea.inventory.client.handler.ProductClientErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ProductFeignConfig
{
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ProductClientErrorDecoder();
    }
}
