package mafia.mafiatogether.domain.status;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class StatusTest {

    private static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    private static final Clock dayIntroTime = Clock.fixed(Instant.parse("2024-01-01T00:00:00.000000Z"), TIME_ZONE);
    private static final Clock dayIntroEndTime = Clock.offset(dayIntroTime, Duration.ofSeconds(2));
    private static final Clock noticeTime = Clock.offset(dayIntroEndTime, Duration.ofSeconds(1));
    private static final Clock noticeEndTime = Clock.offset(noticeTime, Duration.ofSeconds(2));
    private static final Clock dayTime = Clock.offset(noticeEndTime, Duration.ofSeconds(1));
    private static final Clock dayEndTime = Clock.offset(dayTime, Duration.ofSeconds(59));
    private static final Clock voteTime = Clock.offset(dayEndTime, Duration.ofSeconds(1));
    private static final Clock voteEndTime = Clock.offset(voteTime, Duration.ofSeconds(9));
    private static final Clock voteResultTime = Clock.offset(voteEndTime, Duration.ofSeconds(1));
    private static final Clock voteResultEndTime = Clock.offset(voteResultTime, Duration.ofSeconds(2));
    private static final Clock nightIntroTime = Clock.offset(voteResultEndTime, Duration.ofSeconds(1));
    private static final Clock nightIntroEndTime = Clock.offset(nightIntroTime, Duration.ofSeconds(2));
    private static final Clock nightTime = Clock.offset(nightIntroEndTime, Duration.ofSeconds(1));
    private static final Clock nightEndTime = Clock.offset(nightTime, Duration.ofSeconds(39));
    private static final Clock nextDay = Clock.offset(nightEndTime, Duration.ofSeconds(1));

    private Room room;

    @BeforeEach
    void setRoom() {
        room = Room.create(new RoomInfo(3, 1, 0, 1), dayIntroTime);
        Player a = Player.create("A");
        Player b = Player.create("B");
        Player c = Player.create("C");

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
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:01:19.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();
        room.getStatusType(voteResultTime);

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Clock endTime = Clock.offset(nightEndTime, Duration.ofSeconds(1));

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getStatusType(voteResultTime);
        room.getStatusType(nightIntroTime);
        room.getStatusType(nightTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 종료상태_일정_시간_이후_대기상태가_된다() {
        // given
        final Clock endTime = Clock.offset(nightEndTime, Duration.ofSeconds(1));
        final Clock endEndTime = Clock.offset(endTime, Duration.ofSeconds(59));
        final Clock waitTime = Clock.offset(endEndTime, Duration.ofSeconds(1));

        room.modifyStatus(StatusType.DAY, dayIntroTime);
        room.getStatusType(noticeTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();
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
