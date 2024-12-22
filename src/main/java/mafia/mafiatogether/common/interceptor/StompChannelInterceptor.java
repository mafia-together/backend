package mafia.mafiatogether.common.interceptor;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.util.AuthExtractor;
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
import java.util.function.Consumer;

@Component
@Configuration
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private static final String SUBSCRIBE_FORMAT = "/sub/chat/%s";
    private static final String PUBLISHING_FORMAT = "%s/%s/%s";

    private final Map<StompCommand, Consumer<StompHeaderAccessor>> actionByCommand = Map.of(
            StompCommand.SUBSCRIBE, this::consumeWhenSubscribe,
            StompCommand.SEND, this::consumeWhenPublish
    );

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (Objects.isNull(headerAccessor)) {
            return message;
        }
        StompCommand command = headerAccessor.getCommand();
        if (!actionByCommand.containsKey(command)) {
            return message;
        }
        actionByCommand.get(command).accept(headerAccessor);
        return message;
    }

    private void consumeWhenSubscribe(StompHeaderAccessor headerAccessor) {
        String[] information = getInformation(headerAccessor);
        headerAccessor.setDestination(SUBSCRIBE_FORMAT.formatted(information[0]));
    }

    private String[] getInformation(StompHeaderAccessor headerAccessor) {
        String destination = headerAccessor.getDestination();
        int lastIndexOfSlash = getLastIndexOfSlash(destination);
        String code = destination.substring(lastIndexOfSlash + 1);

        return AuthExtractor.extractByCode(code);
    }

    private int getLastIndexOfSlash(String destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination이 존재하지 않습니다.");
        }

        int lastIndexOfSlash = destination.lastIndexOf('/');

        if (lastIndexOfSlash == -1) {
            throw new IllegalArgumentException("올바르지 않은 Destintation 입니다.");
        }

        return lastIndexOfSlash;
    }

    private void consumeWhenPublish(StompHeaderAccessor headerAccessor) {
        String[] information = getInformation(headerAccessor);
        String prefixUrl = headerAccessor.getDestination()
                .substring(0, headerAccessor.getDestination().lastIndexOf('/'));
        headerAccessor.setDestination(PUBLISHING_FORMAT.formatted(prefixUrl, information[0], information[1]));
    }

}