package cl.duoc.caso07.historialclinico;

import org.junit.jupiter.api.Test;
import cl.duoc.caso07.historialclinico.config.OpenApiConfig;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void beanOpenApiGenerado() {
        assertThat(new OpenApiConfig().customOpenAPI()).isNotNull();
    }
}
