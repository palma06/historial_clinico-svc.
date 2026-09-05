package cl.duoc.caso07.historialclinico.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Historial Clínico API")
                        .version("1.0.0")
                        .description("Microservicio Historial Clínico del caso caso07 - MediCare."));
    }
}
