package mafia.mafiatogether.chat.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.BasicAuthResolver;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebsocketPlayerArgumentResolver implements HandlerMethodArgumentResolver {

    private final BasicAuthResolver basicAuthResolver;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PlayerInfo.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final Message<?> message) throws Exception {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        String authorization = headerAccessor.getFirstNativeHeader("Authorization");
        String[] information = basicAuthResolver.resolve(authorization);

        return new PlayerInfoDto(information[0], information[1]);
    }

}
