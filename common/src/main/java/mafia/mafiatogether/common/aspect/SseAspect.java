package mafia.mafiatogether.common.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.domain.SseEmitterSession;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Aspect
@Component
@RequiredArgsConstructor
public class SseAspect {

    private final SseEmitterSession sseEmitterSession;

    @Around("@annotation(mafia.mafiatogether.common.annotation.SseSubscribe)")
    public Object subscribe(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Method method = methodSignature.getMethod();

        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        final Object[] args = joinPoint.getArgs();

        final String[] codeAndName = new String[2];
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

        final SseEmitter sseEmitter = (SseEmitter) joinPoint.proceed();
        sseEmitterSession.save(code, name, sseEmitter);
        sseEmitter.onCompletion(() -> sseEmitterSession.deleteByCodeAndEmitter(code, name));
        sseEmitter.onTimeout(sseEmitter::complete);

        return sseEmitter;
    }

    private boolean hasPlayerInfo(final Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(PlayerInfo.class::isInstance);
    }
}
