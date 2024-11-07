package mafia.mafiatogether.game.domain;

import mafia.mafiatogether.common.infra.InMemorySseEmitterSession;
import mafia.mafiatogether.common.exception.ServerException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
class InMemorySseEmitterSessionTest {

    private InMemorySseEmitterSession inMemorySseEmitterSession;

    @BeforeEach
    void setUp() {
        inMemorySseEmitterSession = new InMemorySseEmitterSession();
    }

    @Test
    void findByCodeAndName을_호출해서_SSE_Emitter를_가져옵니다() {
        // given
        SseEmitter sseEmitter = mock(SseEmitter.class);
        SseEmitter findOutSseEmitter = mock(SseEmitter.class);
        inMemorySseEmitterSession.save("code", "name", sseEmitter);
        inMemorySseEmitterSession.save("code", "name2", findOutSseEmitter);

        // when
        SseEmitter actual = inMemorySseEmitterSession.findByCodeAndName("code", "name2");

        // given
        assertThat(actual).isEqualTo(findOutSseEmitter);
    }

    @Test
    void 존재하지_않는_코드를_입력하면_예외가_발생합니다() {
        // givne when
        ThrowingCallable actual = () -> inMemorySseEmitterSession.findByCodeAndName("code", "name");

        // then
        assertThatThrownBy(actual).isInstanceOf(ServerException.class);
    }

    @Test
    void 존재하지_않는_유저를_입력하면_예외가_발생합니다() {
        // givne
        SseEmitter sseEmitter = mock(SseEmitter.class);
        inMemorySseEmitterSession.save("code", "name2", sseEmitter);

        // when
        ThrowingCallable actual = () -> inMemorySseEmitterSession.findByCodeAndName("code", "name");

        // then
        assertThatThrownBy(actual).isInstanceOf(ServerException.class);
    }

}