package mafia.mafiatogether.common.infra;

import mafia.mafiatogether.common.domain.SseEmitterSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class SseEventPublisherTest {

    @InjectMocks
    private SseEventPublisher sseEventPublisher;

    @Mock
    private SseEmitterSession sseEmitterSession;

    private static final String EVENT_NAME = "EVENT_NAME";
    private static final String CODE = "CODE";
    private SseEmitter SSE_EMITTER_1;
    private SseEmitter SSE_EMITTER_2;
    private SseEmitter SSE_EMITTER_3;

    @BeforeEach
    void setSseEmitters() {
        SSE_EMITTER_1 = mock(SseEmitter.class);
        SSE_EMITTER_2 = mock(SseEmitter.class);
        SSE_EMITTER_3 = mock(SseEmitter.class);
    }

    @Test
    void 모든_구독자에게_같은_이벤트를_발행한다() throws IOException {
        // given
        List<SseEmitter> sseEmitters = List.of(SSE_EMITTER_1, SSE_EMITTER_2, SSE_EMITTER_3);
        when(sseEmitterSession.findByCode(CODE)).thenReturn(sseEmitters);
        String event = "event";

        // when
        sseEventPublisher.publishEventToAllSseClient(CODE, EVENT_NAME, event);

        // then
        verify(SSE_EMITTER_1).send(any(SseEmitter.SseEventBuilder.class));
        verify(SSE_EMITTER_2).send(any(SseEmitter.SseEventBuilder.class));
        verify(SSE_EMITTER_3).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void 모든_구독자에게_개별로_다른_이벤트를_발행한다() throws IOException {
        // given
        List<String> players = List.of("player1","player2","player3");
        String event = "event";
        when(sseEmitterSession.findByCodeAndName(CODE, players.get(0))).thenReturn(SSE_EMITTER_1);
        when(sseEmitterSession.findByCodeAndName(CODE, players.get(1))).thenReturn(SSE_EMITTER_2);
        when(sseEmitterSession.findByCodeAndName(CODE, players.get(2))).thenReturn(SSE_EMITTER_3);

        // when
        for (String player : players) {
            sseEventPublisher.publishEventByCodeAndName(CODE, player, EVENT_NAME, event);
        }

        // then
        verify(SSE_EMITTER_1).send(any(SseEmitter.SseEventBuilder.class));
        verify(SSE_EMITTER_2).send(any(SseEmitter.SseEventBuilder.class));
        verify(SSE_EMITTER_3).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void 코드에_구독한_모든_SSE_연결을_끊는다(){
        // then

        // when
        sseEventPublisher.disconnectSseByCode(CODE);

        // verify
        verify(sseEmitterSession).deleteByCode(CODE);
    }
}