package mafia.mafiatogether.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class InMemorySseEmitterRepository implements SseEmitterRepository {

    private final Map<String, Map<String, SseEmitter>> emitters;

    public InMemorySseEmitterRepository() {
        this.emitters = new ConcurrentHashMap<>();
    }

    @Override
    public void save(final String code, final String name, final SseEmitter sseEmitter) {
        if (!emitters.containsKey(code)) {
            emitters.put(code, new ConcurrentHashMap<>());
        }
        emitters.get(code).put(name, sseEmitter);
    }

    @Override
    public List<SseEmitter> findByCode(String code) {
        if (!emitters.containsKey(code)) {
            return new ArrayList<>();
        }
        return emitters.get(code).values().stream().toList();
    }

    @Override
    public void deleteByCode(String code) {
        emitters.remove(code);
    }

    @Override
    public void deleteByCodeAndEmitter(String code, final String name) {
        if (!emitters.containsKey(code)) {
            return;
        }
        emitters.get(code).remove(name);
    }
}
