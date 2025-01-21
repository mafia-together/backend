package mafia.mafiatogether.common.infra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mafia.mafiatogether.common.domain.SseEmitterSession;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.ServerException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class InMemorySseEmitterSession implements SseEmitterSession {

    private final Map<String, Map<String, SseEmitter>> emitters;

    public InMemorySseEmitterSession() {
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
    public List<SseEmitter> findByCode(final String code) {
        if (!emitters.containsKey(code)) {
            return new ArrayList<>();
        }
        return emitters.get(code).values().stream().toList();
    }

    @Override
    public SseEmitter findByCodeAndName(final String code, final String name) {
        if (!emitters.containsKey(code) || !emitters.get(code).containsKey(name)) {
            throw new ServerException(ExceptionCode.INVALID_PLAYER);
        }
        return emitters.get(code).get(name);
    }

    @Override
    public void deleteByCode(final String code) {
        emitters.remove(code);
    }

    @Override
    public void deleteByCodeAndEmitter(final String code, final String name) {
        if (!emitters.containsKey(code)) {
            return;
        }
        emitters.get(code).remove(name);
    }
}
