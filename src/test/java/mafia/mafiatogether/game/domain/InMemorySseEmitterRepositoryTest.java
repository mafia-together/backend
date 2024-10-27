package mafia.mafiatogether.game.domain;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class InMemorySseEmitterRepositoryTest {

    private InMemorySseEmitterRepository inMemorySseEmitterRepository;

    @BeforeEach
    void setUp() {
        inMemorySseEmitterRepository = new InMemorySseEmitterRepository();
    }

    @Test
    void findByCodeAndName을_호출해서_SSE_Emitter를_가져옵니다() {
        // given
        SseEmitter sseEmitter = mock(SseEmitter.class);
        SseEmitter findOutSseEmitter = mock(SseEmitter.class);
        inMemorySseEmitterRepository.save("code", "name", sseEmitter);
        inMemorySseEmitterRepository.save("code", "name2", findOutSseEmitter);

        // when
        SseEmitter actual = inMemorySseEmitterRepository.findByCodeAndName("code", "name2");

        // given
        assertThat(actual).isEqualTo(findOutSseEmitter);
    }

    @Test
    void 존재하지_않는_코드를_입력하면_예외가_발생합니다() {
        // givne when
        ThrowingCallable actual = () -> inMemorySseEmitterRepository.findByCodeAndName("code", "name");

        // then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_유저를_입력하면_예외가_발생합니다() {
        // givne
        SseEmitter sseEmitter = mock(SseEmitter.class);
        inMemorySseEmitterRepository.save("code", "name2", sseEmitter);

        // when
        ThrowingCallable actual = () -> inMemorySseEmitterRepository.findByCodeAndName("code", "name");

        // then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

}