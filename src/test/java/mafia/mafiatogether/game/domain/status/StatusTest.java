package mafia.mafiatogether.game.domain.status;

import static mafia.mafiatogether.global.Fixture.dayEndTime;
import static mafia.mafiatogether.global.Fixture.dayIntroEndTime;
import static mafia.mafiatogether.global.Fixture.dayIntroTime;
import static mafia.mafiatogether.global.Fixture.dayTime;
import static mafia.mafiatogether.global.Fixture.nextDay;
import static mafia.mafiatogether.global.Fixture.nightEndTime;
import static mafia.mafiatogether.global.Fixture.nightIntroEndTime;
import static mafia.mafiatogether.global.Fixture.nightIntroTime;
import static mafia.mafiatogether.global.Fixture.nightTime;
import static mafia.mafiatogether.global.Fixture.noticeEndTime;
import static mafia.mafiatogether.global.Fixture.noticeTime;
import static mafia.mafiatogether.global.Fixture.voteEndTime;
import static mafia.mafiatogether.global.Fixture.voteResultEndTime;
import static mafia.mafiatogether.global.Fixture.voteResultTime;
import static mafia.mafiatogether.global.Fixture.voteTime;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class StatusTest {

    private static final String PLAYER1 = "A";
    private static final String PLAYER2 = "B";
    private static final String PLAYER3 = "C";

    private Game game;

    @BeforeEach
    void setRoom() {
        Lobby lobby = Lobby.create(new LobbyInfo(3, 1, 0, 1));
        lobby.joinPlayer(PLAYER1);
        lobby.joinPlayer(PLAYER2);
        lobby.joinPlayer(PLAYER3);
        game = Game.create(lobby, dayIntroTime);
        game.distributeRole();
    }

    @Test
    void 게임이_진행되며_상태가_바뀐다() {
        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(game.getStatusType(dayIntroEndTime)).isEqualTo(StatusType.DAY_INTRO);
                    softly.assertThat(game.getStatusType(noticeTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(game.getStatusType(noticeEndTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(game.getStatusType(dayTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(game.getStatusType(dayEndTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(game.getStatusType(voteTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(game.getStatusType(voteEndTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(game.getStatusType(voteResultTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(game.getStatusType(voteResultEndTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(game.getStatusType(nightIntroTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(game.getStatusType(nightIntroEndTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(game.getStatusType(nightTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(game.getStatusType(nightEndTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(game.getStatusType(nextDay)).isEqualTo(StatusType.DAY_INTRO);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = voteResultEndTime + 2_000L;

        game.getStatusType(noticeTime);
        game.getStatusType(dayTime);
        game.getStatusType(voteTime);
        game.getPlayer(PLAYER1).kill();
        game.getPlayer(PLAYER2).kill();
        game.getStatusType(voteResultTime);

        // when & then
        Assertions.assertThat(game.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long nextNoticeTime = nextDay + 3_000L;
        final Long endTime = nextNoticeTime + 3_000L;
        game.getStatusType(noticeTime);
        game.getStatusType(dayTime);
        game.getStatusType(voteTime);
        game.getStatusType(voteResultTime);
        game.getStatusType(nightIntroTime);
        game.getStatusType(nightTime);
        game.getPlayer(PLAYER1).kill();
        game.getPlayer(PLAYER2).kill();
        game.getStatusType(nextDay);
        game.getStatusType(nextNoticeTime);

        // when & then
        Assertions.assertThat(game.getStatusType(endTime)).isEqualTo(StatusType.END);

    }
}
