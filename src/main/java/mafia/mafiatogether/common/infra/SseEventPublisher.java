package mafia.mafiatogether.common.infra;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.domain.SseEmitterSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SseEventPublisher {

    public static final long SECOND_30 = 30_000L;
    private final SseEmitterSession sseEmitterSession;

    public void publishEventToAllSseClient(final String code, final String eventName, final Object event) {
        List<SseEmitter> emitters = sseEmitterSession.findByCode(code);
        for (SseEmitter emitter : emitters) {
            SseEventBuilder builder = getSseEventBuilder(eventName, event);
            publishSseEvent(emitter, builder);
        }
    }

    private void publishSseEvent(final SseEmitter emitter, final SseEventBuilder eventBuilder) {
        try {
            emitter.send(eventBuilder);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    public static SseEventBuilder getSseEventBuilder(final String eventName, final Object event) {
        return SseEmitter.event()
                .name(eventName)
                .data(event)
                .reconnectTime(SECOND_30);
    }

    public void publishEventByCodeAndName(
            final String code,
            final String participantName,
            final String eventName,
            final Object event
    ) {
        SseEmitter sseEmitter = sseEmitterSession.findByCodeAndName(code, participantName);
        SseEventBuilder eventBuilder = getSseEventBuilder(eventName, event);
        publishSseEvent(sseEmitter, eventBuilder);
    }

    public void disconnectSseByCode(final String code) {
        sseEmitterSession.deleteByCode(code);
    }
}
