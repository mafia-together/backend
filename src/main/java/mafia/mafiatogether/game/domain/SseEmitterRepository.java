package mafia.mafiatogether.game.domain;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {


    void save(final String code, final SseEmitter sseEmitter);

    List<SseEmitter> findByCode(final String code);

    void deleteByCode(final String code);

    void deleteByCodeAndEmitter(final String code, final SseEmitter sseEmitter);
}
