package mafia.mafiatogether.game.domain;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {


    void save(final String code, final SseEmitter sseEmitter);
}
