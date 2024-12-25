package mafia.mafiatogether.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.ui.WebsocketPlayerArgumentResolver;
import mafia.mafiatogether.common.interceptor.ChatInterceptor;
import mafia.mafiatogether.common.interceptor.PathMatcherInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatInterceptor chatInterceptor;
    private final WebsocketPlayerArgumentResolver websocketPlayerArgumentResolver;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins(
                        "https://dev.mafia-together.com",
                        "https://mafia-together.com",
                        "http://localhost:5173",
                        "https://localhost:5173"
                );
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(websocketPlayerArgumentResolver);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registry) {
        registry.interceptors(
                new PathMatcherInterceptor(chatInterceptor)
                        .includePathPattern("/sub/chat/**", StompCommand.SUBSCRIBE)
                        .includePathPattern("/pub/chat/**", StompCommand.MESSAGE)
                        .includePathPattern("/pub/chat/**", StompCommand.SEND)
        );
    }

}
