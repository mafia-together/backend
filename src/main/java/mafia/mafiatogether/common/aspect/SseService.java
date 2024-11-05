package mafia.mafiatogether.common.aspect;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import mafia.mafiatogether.common.domain.SseEmitterRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class SseService {

    public static final long HOURS_12 = 43200_000L;
    public static final long SECOND_30 = 30_000L;
    private final SseEmitterRepository sseEmitterRepository;

    @Around("@annotation(mafia.mafiatogether.common.annotation.SseSubscribe)")
    public Object subscribe(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        String[] codeAndName = new String[2];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (hasPlayerInfo(parameterAnnotations[i])) {
                PlayerInfoDto playerInfoDto = (PlayerInfoDto) args[i];
                codeAndName[0] = playerInfoDto.code();
                codeAndName[1] = playerInfoDto.name();
                break;
            }
        }

        if (codeAndName[0] == null || codeAndName[1] == null) {
            throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
        }

        final String code = codeAndName[0];
        final String name = codeAndName[1];

        SseEmitter sseEmitter = (SseEmitter) joinPoint.proceed();
        sseEmitterRepository.save(code, name, sseEmitter);
        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteByCodeAndEmitter(code, name));
        sseEmitter.onTimeout(sseEmitter::complete);

        return sseEmitter;
    }

    private boolean hasPlayerInfo(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(PlayerInfo.class::isInstance);
    }

    public static SseEmitter getSseEmitter(final String name, final Object event) throws IOException {
        SseEmitter sseEmitter = new SseEmitter(HOURS_12);
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(name)
                .data(event)
                .reconnectTime(SECOND_30);
        sseEmitter.send(sseEventBuilder);
        return sseEmitter;
    }
}
