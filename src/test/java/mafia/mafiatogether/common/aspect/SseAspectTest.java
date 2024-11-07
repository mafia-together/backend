package mafia.mafiatogether.common.aspect;

import mafia.mafiatogether.common.domain.SseEmitterSession;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Import(SseAspectTestService.class)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SseAspectTest {

    @Autowired
    private SseAspectTestService sseAspectTestService;

    @MockBean
    private SseEmitterSession sseEmitterSession;

    @Test
    void SSE_구독을_한다(){
        // given
        final String code = "code";
        final String name = "name";
        final PlayerInfoDto playerInfoDto = new PlayerInfoDto(code, name);

        // when
        final SseEmitter actual = sseAspectTestService.subscribe(playerInfoDto);

        // then
        verify(sseEmitterSession).save(code, name, actual);
        verify(actual).onCompletion(any());
        verify(actual).onTimeout(any());
    }
}