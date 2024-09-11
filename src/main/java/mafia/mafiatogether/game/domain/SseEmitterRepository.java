package mafia.mafiatogether.game.domain;

import java.util.List;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {


    void save(final String code, final SseEmitter sseEmitter);

    List<SseEmitter> get(final String code);
}
