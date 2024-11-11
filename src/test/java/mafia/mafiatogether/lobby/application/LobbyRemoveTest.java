package mafia.mafiatogether.lobby.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;

import mafia.mafiatogether.lobby.application.dto.event.DeleteLobbyEvent;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class LobbyRemoveTest {

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private LobbyRemoveService lobbyRemoveService;

    @Test
    void 스케줄러_동작_테스트() {
        Lobby mockLobby = Mockito.mock(Lobby.class);
        given(mockLobby.getLastUpdateTime()).willReturn(Instant.now().getEpochSecond() - 3700);
        given(mockLobby.getCode()).willReturn("1234567890");
        given(lobbyRepository.findAll()).willReturn(List.of(mockLobby));

        lobbyRemoveService.remove();

        verify(applicationEventPublisher, times(1)).publishEvent(any(DeleteLobbyEvent.class));
    }
}
