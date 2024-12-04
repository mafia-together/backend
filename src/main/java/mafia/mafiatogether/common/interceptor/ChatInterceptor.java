package mafia.mafiatogether.common.interceptor;

import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.util.AuthExtractor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert headerAccessor != null;

        String authorization = String.valueOf(headerAccessor.getFirstNativeHeader("Authorization"));

        if (authorization == null) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }
        if (!authorization.startsWith("Basic")) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }

        //검증
        String[] information = AuthExtractor.extractByAuthorization(authorization);
        return message;
    }

}
