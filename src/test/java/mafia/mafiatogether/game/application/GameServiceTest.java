package mafia.mafiatogether.game.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;

import io.restassured.RestAssured;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.game.domain.SseEmitterRepository;
import mafia.mafiatogether.game.domain.status.DayIntroStatus;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.global.RedisTestConfig;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(RedisTestConfig.class)
@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GameServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected LobbyRepository lobbyRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @MockBean
    private SseEmitterRepository sseEmitterRepository;

    @MockBean
    private JobTargetRepository jobTargetRepository;

    private static Game STATUSCHANGEDGAME;
    private static Game NOTCHANGEDGAME;
    private static Long now;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setGames() {
        now = Clock.systemDefaultZone().millis();
        STATUSCHANGEDGAME = new Game(
                "STATUSCHANGEDGAME",
                DayIntroStatus.create(now - 10_000L),
                LobbyInfo.of(5, 2, 1, 1),
                "master",
                new PlayerCollection(),
                DayIntroStatus.create(now - 10_000L)
        );
        NOTCHANGEDGAME = new Game(
                "NOTCHANGEDGAME",
                DayIntroStatus.create(now + 10_000L),
                LobbyInfo.of(5, 2, 1, 1),
                "master",
                new PlayerCollection(),
                DayIntroStatus.create(now + 10_000L)
        );
        lobbyRepository.save(Lobby.create(STATUSCHANGEDGAME.getCode(), LobbyInfo.of(5, 2, 1, 1)));
        lobbyRepository.save(Lobby.create(NOTCHANGEDGAME.getCode(), LobbyInfo.of(5, 2, 1, 1)));
        gameRepository.save(STATUSCHANGEDGAME);
        gameRepository.save(NOTCHANGEDGAME);
    }

    @Test
    void 스케쥴러에_의해_방의_시간이_변경된다() {
        // given
        JobTarget mockedJobTarget = Mockito.mock(JobTarget.class);
        Mockito.when(sseEmitterRepository.get(any())).thenReturn(List.of());
        Mockito.when(jobTargetRepository.findById(any())).thenReturn(Optional.of(mockedJobTarget));

        // when & then
        await().atMost(Duration.ofMillis(4_000L))
                .untilAsserted(() -> assertStatusType());
    }

    private void assertStatusType() {
        final StatusType actualChanged = gameRepository.findById(STATUSCHANGEDGAME.getCode()).get().getStatus()
                .getType();
        final StatusType actualNotChanged = gameRepository.findById(NOTCHANGEDGAME.getCode()).get().getStatus()
                .getType();

        assertSoftly(
                softly -> {
                    softly.assertThat(actualChanged).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(actualNotChanged).isEqualTo(StatusType.DAY_INTRO);
                }
        );
    }

//    @Test
//    void 상태가_변경시_이벤트가_발행된다() throws IOException {
//        // given
//        JobTarget mockedJobTarget = Mockito.mock(JobTarget.class);
//        Mockito.when(sseEmitterRepository.get(any())).thenReturn(List.of());
//        Mockito.when(jobTargetRepository.findById(any())).thenReturn(Optional.of(mockedJobTarget));
//
//        // when
//        gameService.changeStatus();
//
//        // then
//        Mockito.verify(sseEmitterRepository, Mockito.times(1)).get(STATUSCHANGEDGAME.getCode());
//        Mockito.verify(sseEmitterRepository, Mockito.times(0)).get(NOTCHANGEDGAME.getCode());
//    }
}
