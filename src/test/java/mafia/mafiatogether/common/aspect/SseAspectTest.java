package mafia.mafiatogether.common.aspect;

import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.domain.SseEmitterSession;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class SseAspectTest {

    @InjectMocks
    private SseAspect sseAspect;

    @Mock
    private SseEmitterSession sseEmitterSession;

    @Test
    void SSE_구독을_한다() throws Throwable {
        // given
        final String code = "code";
        final String name = "name";
        PlayerInfoDto playerInfoDto = new PlayerInfoDto(code, name);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        Method method = mock(Method.class);
        Annotation[][] annotations = new Annotation[1][1];
        annotations[0][0] = mock(PlayerInfo.class);

        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(methodSignature.getMethod()).willReturn(method);
        given(joinPoint.getArgs()).willReturn(new Object[]{playerInfoDto});
        given(method.getParameterAnnotations()).willReturn(annotations);


        SseEmitter sseEmitter = mock(SseEmitter.class);
        given(joinPoint.proceed()).willReturn(sseEmitter);

        // when
        SseEmitter actual = (SseEmitter) sseAspect.subscribe(joinPoint);

        // then
        verify(sseEmitterSession).save(code, name, sseEmitter);
        assertThat(actual).isEqualTo(sseEmitter);
        verify(sseEmitter).onCompletion(any());
        verify(sseEmitter).onTimeout(any());
    }
}