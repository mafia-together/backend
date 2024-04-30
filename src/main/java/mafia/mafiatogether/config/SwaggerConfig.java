package mafia.mafiatogether.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import mafia.mafiatogether.controller.PlayerInfo;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "Basic",
        in = SecuritySchemeIn.HEADER,
        name = AUTHORIZATION
)
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig()
                .addAnnotationsToIgnore(PlayerInfo.class);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Mafia Together 문서")
                .version("1.0.0");
    }
}
