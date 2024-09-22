package mafia.mafiatogether.config;

import mafia.mafiatogether.common.AuthExtractor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Configuration
public class StompChannelInterceptor implements ChannelInterceptor {

    private static final String SUBSCRIBE_FORMAT = "/sub/chat/%s";
    private static final String PUBLISHING_FORMAT = "/pub/chat/%s/%s";
    private static final Map<StompCommand, String> formatMapper = Map.of(
            StompCommand.SUBSCRIBE, SUBSCRIBE_FORMAT,
            StompCommand.SEND, PUBLISHING_FORMAT
    );

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.isNull(headerAccessor)) {
            return message;
        }
        StompCommand command = headerAccessor.getCommand();
        if (command != StompCommand.SUBSCRIBE && command != StompCommand.SEND) {
            return message;
        }
        String[] information = getInformation(headerAccessor);
        headerAccessor.setDestination(String.format(formatMapper.get(command), information[0], information[1]));
        return message;
    }

    private static String[] getInformation(StompHeaderAccessor headerAccessor) {
        String destination = headerAccessor.getDestination();
        int lastIndexOfSlash = getLastIndexOfSlash(destination);
        String code = destination.substring(lastIndexOfSlash + 1);

        return AuthExtractor.extractByCode(code);
    }

    private static int getLastIndexOfSlash(String destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination이 존재하지 않습니다.");
        }

        int lastIndexOfSlash = destination.lastIndexOf('/');

        if (lastIndexOfSlash == -1) {
            throw new IllegalArgumentException("올바르지 않은 Destintation 입니다.");
        }

        return lastIndexOfSlash;
    }

}