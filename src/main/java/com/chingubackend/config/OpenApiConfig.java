package com.chingubackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Chingu API", version = "v1"),
        servers = {
                @Server(url = "https://chinguchingu.kro.kr", description = "Production Server"),
                @Server(url = "http://localhost:8080", description = "Local Server")
        }
)
@Configuration
public class OpenApiConfig {
}