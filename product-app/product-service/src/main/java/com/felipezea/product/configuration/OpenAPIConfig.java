package com.felipezea.product.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Marks this class as a Spring configuration class
@SecurityScheme(
        name = "apiKey",                       // Name used to reference the security scheme
        type = SecuritySchemeType.APIKEY,      // Specifies that we are using an API key type
        in = SecuritySchemeIn.HEADER,          // The API key will be passed in the request header
        paramName = "X-API-KEY"                // Name of the header expected by the service
)
public class OpenAPIConfig {

    /**
     * Defines and configures the OpenAPI (Swagger) documentation.
     * This bean customizes metadata and applies the defined API Key security requirement.
     *
     * @return an OpenAPI instance used by Swagger UI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Apply the API key security requirement to all operations
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                // Basic API documentation information shown in Swagger UI
                .info(new Info()
                        .title("Product Service API")
                        .version("1.0")
                        .description("API documentation with API key security"));
    }
}
