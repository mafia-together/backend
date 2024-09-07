package mafia.mafiatogether.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class InMemorySseEmitterRepository implements SseEmitterRepository {

    private final Map<String, List<SseEmitter>> emitters;

    public InMemorySseEmitterRepository() {
        this.emitters = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final String code, final SseEmitter sseEmitter) {
        if (!emitters.containsKey(code)) {
            emitters.put(code, new ArrayList<>());
        }
        emitters.get(code).add(sseEmitter);
    }

    @Override
    public List<SseEmitter> get(String code) {
        return emitters.get(code);
    }
}
