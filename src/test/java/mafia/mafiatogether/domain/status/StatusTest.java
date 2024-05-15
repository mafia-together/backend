package mafia.mafiatogether.domain.status;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Clock;
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
    private static final Clock roomCreatedTime = Clock.fixed(Instant.parse("2024-01-01T00:00:00.000000Z"), TIME_ZONE);
    private static final Clock dayIntroEndTime = Clock.fixed(Instant.parse("2024-01-01T00:00:02.000000Z"), TIME_ZONE);
    private static final Clock noticeTime = Clock.fixed(Instant.parse("2024-01-01T00:00:03.000000Z"), TIME_ZONE);
    private static final Clock noticeEndTime = Clock.fixed(Instant.parse("2024-01-01T00:00:05.000000Z"), TIME_ZONE);
    private static final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:00:06.000000Z"), TIME_ZONE);
    private static final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:05.000000Z"), TIME_ZONE);
    private static final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:06.000000Z"), TIME_ZONE);
    private static final Clock voteEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:15.000000Z"), TIME_ZONE);
    private static final Clock voteResultTime = Clock.fixed(Instant.parse("2024-01-01T00:01:16.000000Z"), TIME_ZONE);
    private static final Clock voteResultEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:18.000000Z"), TIME_ZONE);
    private static final Clock nightIntroTime = Clock.fixed(Instant.parse("2024-01-01T00:01:19.000000Z"), TIME_ZONE);
    private static final Clock nightIntroEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:21.000000Z"), TIME_ZONE);
    private static final Clock nightTime = Clock.fixed(Instant.parse("2024-01-01T00:01:22.000000Z"), TIME_ZONE);
    private static final Clock nightEndTime = Clock.fixed(Instant.parse("2024-01-01T00:02:01.000000Z"), TIME_ZONE);
    private static final Clock nextDay = Clock.fixed(Instant.parse("2024-01-01T00:02:02.000000Z"), TIME_ZONE);

    private Room room;

    @BeforeEach
    void setRoom() {
        room = Room.create(new RoomInfo(3, 1, 0, 1), roomCreatedTime);
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
        room.modifyStatus(StatusType.DAY, roomCreatedTime);

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

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
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
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:02:02.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
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
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:01:19.000000Z"), TIME_ZONE);
        final Clock endEndTime = Clock.fixed(Instant.parse("2024-01-01T00:02:18.000000Z"), TIME_ZONE);
        final Clock waitTime = Clock.fixed(Instant.parse("2024-01-01T00:02:19.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
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
