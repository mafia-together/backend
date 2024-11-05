package mafia.mafiatogether.common.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.domain.SseEmitterRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SseEventPublisher {

    public static final long SECOND_30 = 30_000L;
    private final SseEmitterRepository sseEmitterRepository;

    public void publishEventToAllSseClient(final String code, final String eventName, final Object event) {
        List<SseEmitter> emitters = sseEmitterRepository.findByCode(code);
        for (SseEmitter emitter : emitters) {
            SseEmitter.SseEventBuilder builder = getSseEventBuilder(eventName, event);
            publishSseEvent(emitter, builder);
        }
    }

    private void publishSseEvent(final SseEmitter emitter, final SseEmitter.SseEventBuilder eventBuilder) {
        try {
            emitter.send(eventBuilder);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    public static SseEmitter.SseEventBuilder getSseEventBuilder(final String eventName, final Object event) {
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
        SseEmitter sseEmitter = sseEmitterRepository.findByCodeAndName(code, participantName);
        SseEmitter.SseEventBuilder eventBuilder = getSseEventBuilder(eventName, event);
        publishSseEvent(sseEmitter, eventBuilder);
    }

    public void disconnectSseByCode(final String code) {
        sseEmitterRepository.deleteByCode(code);
    }
}
