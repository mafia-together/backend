package mafia.mafiatogether.common.domain;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterSession {

    void save(final String code, final String name, final SseEmitter sseEmitter);

    List<SseEmitter> findByCode(final String code);

    SseEmitter findByCodeAndName(final String code, final String name);

    void deleteByCode(final String code);

    void deleteByCodeAndEmitter(final String code, final String name);

}
