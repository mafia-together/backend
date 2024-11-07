package mafia.mafiatogether.lobby.application;

import mafia.mafiatogether.common.infra.SseEventPublisher;
import mafia.mafiatogether.lobby.application.dto.event.ParticipantJoinEvent;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class LobbyEventListenerTest {

    @InjectMocks
    private LobbyEventListener lobbyEventListener;

    @Mock
    private SseEventPublisher sseEventPublisher;

    @Test
    void 새로운_유저가_접속하면_각각의_유저에게_SSE_Event를_발송합니다(){
        // given
        LobbyInfo lobbyInfo = LobbyInfo.of(3, 1, 1, 1);
        String roomCode = "code";
        String nameOfPlayer1 = "name1";
        String nameOfPlayer2 = "name2";
        final String newPlayer = "newPlayer";
        String eventName = "lobbyInfo";
        Lobby lobby = Lobby.create(roomCode, lobbyInfo);
        lobby.joinPlayer(nameOfPlayer1);
        lobby.joinPlayer(nameOfPlayer2);

        // when
        lobbyEventListener.handleJoinEvent(new ParticipantJoinEvent(lobby, newPlayer));

        // then
        verify(sseEventPublisher).publishEventByCodeAndName(eq(roomCode), eq(nameOfPlayer1), eq(eventName), any());
        verify(sseEventPublisher).publishEventByCodeAndName(eq(roomCode), eq(nameOfPlayer2), eq(eventName), any());
        verify(sseEventPublisher, times(0)).publishEventByCodeAndName(eq(roomCode), eq(newPlayer), eq(eventName), any());
    }
}