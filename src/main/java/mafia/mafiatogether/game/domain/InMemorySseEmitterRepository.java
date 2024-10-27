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
    public List<SseEmitter> findByCode(String code) { // TODO : 이거를 Name 이랑 Mapping해서 반환하는 것은 어떠신가요?
        if (!emitters.containsKey(code)) {
            return new ArrayList<>();
        }
        return emitters.get(code).values().stream().toList();
    }

    @Override
    public SseEmitter findByCodeAndName(String code, String name) {
        if (!emitters.containsKey(code) || !emitters.get(code).containsKey(name)) {
            throw new IllegalArgumentException("sse emitter of user not found");
        }
        return emitters.get(code).get(name);
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
