package co.com.example.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("API REST Usuarios - Mongo + JWT")
            .version("1.1.0")
            .description("API con registro/login, CRUD, recuperación de contraseña y Swagger"));
    }
}
