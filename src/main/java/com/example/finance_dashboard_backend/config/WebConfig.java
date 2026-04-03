package com.example.finance_dashboard_backend.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
    OpenAPI financeDashboardOpenApi(){
        return new OpenAPI().info(new Info()
                .title("Finance Dashboard Backend API")
                .version("1.0")
                .description("Role-aware finance backend with JWT auth, exports, snapshots, and scoped analytics.")
                .contact(new Contact().name("Codex Assignment Implementation")));
    }
}
