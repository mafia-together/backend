package mafia.mafiatogether.lobby.application;

import mafia.mafiatogether.game.application.LobbyEventListener;
import mafia.mafiatogether.game.domain.SseEmitterRepository;
import mafia.mafiatogether.lobby.application.dto.event.ParticipantJoinEvent;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class LobbyEventListenerTest {

    @InjectMocks
    private LobbyEventListener lobbyEventListener;

    @Mock
    private SseEmitterRepository sseEmitterRepository;

    @Test
    void 새로운_유저가_접속하면_각각의_유저에게_SSE_Event를_발송합니다() throws Exception {
        // given
        List<SseEmitter> sseEmitters = List.of(mock(SseEmitter.class), mock(SseEmitter.class));
        LobbyInfo lobbyInfo = LobbyInfo.of(3, 1, 1, 1);
        String roomCode = "code";
        String nameOfPlayer1 = "name1";
        String nameOfPlayer2 = "name2";
        Lobby lobby = Lobby.create(roomCode, lobbyInfo);
        lobby.joinPlayer(nameOfPlayer1);
        lobby.joinPlayer(nameOfPlayer2);
        given(sseEmitterRepository.findByCodeAndName(roomCode, nameOfPlayer1)).willReturn(sseEmitters.get(0));
        given(sseEmitterRepository.findByCodeAndName(roomCode, nameOfPlayer2)).willReturn(sseEmitters.get(1));

        // when
        lobbyEventListener.handleJoinEvent(new ParticipantJoinEvent(lobby, "newParticipant"));

        // then
        verify(sseEmitterRepository).findByCodeAndName(roomCode, nameOfPlayer1);
        verify(sseEmitterRepository).findByCodeAndName(roomCode, nameOfPlayer2);
        verify(sseEmitters.get(0)).send(any(SseEmitter.SseEventBuilder.class));
        verify(sseEmitters.get(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

}