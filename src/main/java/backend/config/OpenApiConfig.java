package backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Brgy Capri API")
                        .version("1.0"))
                .components(new Components().addSecuritySchemes(
                        "accessTokenCookie",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("accessToken")
                                .description("JWT access token cookie set by the /login endpoint")
                ));
    }

    @Bean
    public OperationCustomizer removeJsonFromRouteController() {
        return (operation, handlerMethod) -> {
            if (operation.getRequestBody() != null
                    && handlerMethod.getBeanType()
                        .getSimpleName().equals("RouteController")) {
                operation.getRequestBody()
                        .getContent()
                        .remove("application/json");
            }
            return operation;
        };
    }
}