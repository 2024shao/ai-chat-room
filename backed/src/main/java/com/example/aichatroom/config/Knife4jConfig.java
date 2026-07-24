package com.example.aichatroom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer");

        return new OpenAPI()
                .info(new Info()
                        .title("AI Chat Room API 文档")
                        .version("1.0.0")
                        .description("AI Chat Room 在线聊天室接口文档"))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("Bearer", securityScheme);
    }
}