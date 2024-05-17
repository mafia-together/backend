package mafia.mafiatogether.domain.status;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Clock;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class StatusTest {

    private static final Long dayIntroTime = Clock.systemDefaultZone().millis();
    private static final Long dayIntroEndTime = dayIntroTime + 2_000L;
    private static final Long noticeTime = dayIntroEndTime + 1_000L;
    private static final Long noticeEndTime = noticeTime + 2_000L;
    private static final Long dayTime = noticeEndTime + 1_000L;
    private static final Long dayEndTime = dayTime + 59_000L;
    private static final Long voteTime = dayEndTime + 1_000L;
    private static final Long voteEndTime = voteTime + 9_000L;
    private static final Long voteResultTime = voteEndTime + 1_000L;
    private static final Long voteResultEndTime = voteResultTime + 2_000L;
    private static final Long nightIntroTime = voteResultEndTime + 1_000L;
    private static final Long nightIntroEndTime = nightIntroTime + 2_000L;
    private static final Long nightTime = nightIntroEndTime + 1_000L;
    private static final Long nightEndTime = nightTime + 39_000L;
    private static final Long nextDay = nightEndTime + 2_000L;
    private static final String PLAYER1 = "A";
    private static final String PLAYER2 = "B";
    private static final String PLAYER3 = "C";

    private Room room;

    @BeforeEach
    void setRoom() {
        room = Room.create(new RoomInfo(3, 1, 0, 1), dayIntroTime);
        Player a = Player.create(PLAYER1);
        Player b = Player.create(PLAYER2);
        Player c = Player.create(PLAYER3);

        room.joinPlayer(a.getName());
        room.joinPlayer(b.getName());
        room.joinPlayer(c.getName());
    }

    @Test
    void 게임이_진행되며_상태가_바뀐다() {
        // given
        room.modifyStatus(StatusType.DAY, dayIntroTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(dayIntroEndTime)).isEqualTo(StatusType.DAY_INTRO);
                    softly.assertThat(room.getStatusType(noticeTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(noticeEndTime)).isEqualTo(StatusType.NOTICE);
                    softly.assertThat(room.getStatusType(dayTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(dayEndTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(voteTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteEndTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteResultTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(voteResultEndTime)).isEqualTo(StatusType.VOTE_RESULT);
                    softly.assertThat(room.getStatusType(nightIntroTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(nightIntroEndTime)).isEqualTo(StatusType.NIGHT_INTRO);
                    softly.assertThat(room.getStatusType(nightTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nightEndTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nextDay)).isEqualTo(StatusType.DAY_INTRO);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = voteResultEndTime + 2_000L;

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(voteResultTime);

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Long endTime = nightEndTime + 1_000L;

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getStatusType(voteResultTime);
        room.getStatusType(nightIntroTime);
        room.getStatusType(nightTime);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 종료상태_일정_시간_이후_대기상태가_된다() {
        // given
        final Long endTime = nightEndTime + 1_000L;
        final Long endEndTime = endTime + 59_000L;
        final Long waitTime = endEndTime + 1_000L;

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer(PLAYER1).kill();
        room.getPlayer(PLAYER2).kill();
        room.getStatusType(voteResultTime);
        room.getStatusType(endTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(endEndTime)).isEqualTo(StatusType.END);
                    softly.assertThat(room.getStatusType(waitTime)).isEqualTo(StatusType.WAIT);
                }
        );
    }
}
