package mafia.mafiatogether.common.interceptor;

import org.springframework.messaging.simp.stomp.StompCommand;

public record StompMapping(
        String destination,
        StompCommand command
) {
}
