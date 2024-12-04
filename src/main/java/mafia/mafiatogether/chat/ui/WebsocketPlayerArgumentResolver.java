package mafia.mafiatogether.chat.ui;

import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.common.util.AuthExtractor;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class WebsocketPlayerArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PlayerInfo.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final Message<?> message) throws Exception {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        String authorization = headerAccessor.getFirstNativeHeader("Authorization");
        if (authorization == null) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }
        if (!authorization.startsWith("Basic")) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }

        String[] information = AuthExtractor.extractByAuthorization(authorization);

        return new PlayerInfoDto(information[0], information[1]);
    }

}
