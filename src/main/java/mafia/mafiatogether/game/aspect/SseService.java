package mafia.mafiatogether.game.aspect;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.game.application.dto.response.GameStatusResponse;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.SseEmitterRepository;
import mafia.mafiatogether.game.domain.status.StatusType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class SseService {

    private static final String SSE_STATUS = "gameStatus";
    public static final long HOURS_12 = 43200_000L;
    public static final long SECOND_30 = 30_000L;
    private final SseEmitterRepository sseEmitterRepository;
    private final GameRepository gameRepository;

    @Around("@annotation(mafia.mafiatogether.game.annotation.SseSubscribe)")
    public ResponseEntity<SseEmitter> subscribe(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        String[] codeAndName = new String[2];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            findCodeOrName(parameterAnnotations[i], args[i], codeAndName);
        }

        String code = codeAndName[0];
        String name = codeAndName[1];
        SseEmitter sseEmitter = createSseEmitter(code, name);
        sseEmitterRepository.save(code, name, sseEmitter);
        return ResponseEntity.ok(sseEmitter);
    }

    private void findCodeOrName(Annotation[] annotations, Object argument, String[] codeAndName) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof PlayerInfo)) {
                continue;
            }
            PlayerInfoDto playerInfoDto = (PlayerInfoDto) argument;
            codeAndName[0] = playerInfoDto.code();
            codeAndName[1] = playerInfoDto.name();
            return;
        }
        throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
    }

    private SseEmitter createSseEmitter(String code, String name) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(HOURS_12);
        sseEmitter.send(getSseEvent(code));
        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteByCodeAndEmitter(code, name));
        sseEmitter.onTimeout(sseEmitter::complete);
        return sseEmitter;
    }

    private SseEmitter.SseEventBuilder getSseEvent(final String code) {
        Optional<Game> game = gameRepository.findById(code);
        return game.map(value -> getSseEventBuilder(value.getStatus().getType()))
                .orElseGet(() -> getSseEventBuilder(StatusType.WAIT));
    }

    private static SseEmitter.SseEventBuilder getSseEventBuilder(final StatusType statusType) {
        return SseEmitter.event()
                .name(SSE_STATUS)
                .data(new GameStatusResponse(statusType))
                .reconnectTime(SECOND_30);
    }
}
