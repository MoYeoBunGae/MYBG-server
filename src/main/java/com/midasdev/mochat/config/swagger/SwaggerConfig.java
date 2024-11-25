package com.midasdev.mochat.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Mochat API", version = "1.0"),
        servers = {
                @Server(url = "https://app.testsvc-avl87.store", description = "mochat dev 서버"),
                @Server(url = "http://localhost:8080", description = "mochat local 서버")
        }
)
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token Authentication"
)
public class SwaggerConfig {

}
