package mafia.mafiatogether.common.config;

import mafia.mafiatogether.common.resolver.PlayerArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PlayerArgumentResolver());
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://dev.mafia-together.com",
                        "https://mafia-together.com",
                        "http://localhost:5173",
                        "https://localhost:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION);
        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
