package com.nyxn.ecommerce.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NYXN E-Commerce API")
                        .version("1.0.0")
                        .description("API RESTful para gestión de catálogo de productos — Prueba Técnica Full Stack")
                        .contact(new Contact()
                                .name("NYXN Engineering Team")
                                .email("tech@nyxn.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://api.nyxn.com").description("Production (GCP Cloud Run)")
                ));
    }
}
