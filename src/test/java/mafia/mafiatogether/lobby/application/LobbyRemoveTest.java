package mafia.mafiatogether.lobby.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class LobbyRemoveTest {

    @MockBean
    private LobbyRepository lobbyRepository;
    @Autowired
    private LobbyRemoveService lobbyRemoveService;

    @Test
    void 스케줄러_동작_테스트() {
        Lobby mockLobby = Mockito.mock(Lobby.class);
        Mockito.when(mockLobby.getLastUpdateTime()).thenReturn(Instant.now().getEpochSecond() - 3700);
        Mockito.when(mockLobby.getCode()).thenReturn("1234567890");
        Mockito.when(lobbyRepository.findAll()).thenReturn(List.of(mockLobby));

        lobbyRemoveService.remove();

        verify(lobbyRepository, times(1)).deleteById(mockLobby.getCode());
    }
}
