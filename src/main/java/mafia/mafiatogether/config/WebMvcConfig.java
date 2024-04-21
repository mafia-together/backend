package mafia.mafiatogether.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.controller.PlayerArgumentResolver;
import mafia.mafiatogether.controller.RequestDecrypter;
import mafia.mafiatogether.controller.RoomArgumentResolver;
import mafia.mafiatogether.domain.RoomManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestDecrypter requestDecrypter;
    private final RoomManager roomManager;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PlayerArgumentResolver(requestDecrypter));
        resolvers.add(new RoomArgumentResolver(roomManager, requestDecrypter));
    }
}
