package mafia.mafiatogether.game.application;

import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @Test
    void 스케쥴러에_의해_방의_시간이_변경된다() {
        // given
        Game changedGame = mock(Game.class);
        Game notChangedGame = mock(Game.class);
        when(gameRepository.findAll()).thenReturn(List.of(changedGame, notChangedGame));
        when(notChangedGame.getStatusType(anyLong())).thenReturn(StatusType.WAIT);
        when(notChangedGame.isStatusChanged()).thenReturn(false);
        when(changedGame.getStatusType(anyLong())).thenReturn(StatusType.DAY);
        when(changedGame.isStatusChanged()).thenReturn(true);

        // when
        gameService.changeStatus();

        // then
        assertSoftly(
                softly -> {
                    verify(gameRepository).save(changedGame);
                }
        );
    }
}
