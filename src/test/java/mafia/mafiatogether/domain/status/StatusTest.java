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
        final Clock dayIntroEndTime = Clock.fixed(Instant.parse("2024-01-01T00:00:02.000000Z"), TIME_ZONE);
        final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:00:03.000000Z"), TIME_ZONE);
        final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:02.000000Z"), TIME_ZONE);
        final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:03.000000Z"), TIME_ZONE);
        final Clock voteEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:12.000000Z"), TIME_ZONE);
        final Clock nightTime = Clock.fixed(Instant.parse("2024-01-01T00:01:13.000000Z"), TIME_ZONE);
        final Clock nightEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:53.000000Z"), TIME_ZONE);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(dayIntroEndTime)).isEqualTo(StatusType.DAY_INTRO);
                    softly.assertThat(room.getStatusType(dayTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(dayEndTime)).isEqualTo(StatusType.DAY);
                    softly.assertThat(room.getStatusType(voteTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteEndTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(nightTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nightEndTime)).isEqualTo(StatusType.NIGHT);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:00:03.000000Z"), TIME_ZONE);
        final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:03.000000Z"), TIME_ZONE);
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:01:13.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 밤_이후_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:00:03.000000Z"), TIME_ZONE);
        final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:03.000000Z"), TIME_ZONE);
        final Clock nightTime = Clock.fixed(Instant.parse("2024-01-01T00:01:13.000000Z"), TIME_ZONE);
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:01:53.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getStatusType(nightTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();

        // when & then
        Assertions.assertThat(room.getStatusType(endTime)).isEqualTo(StatusType.END);
    }

    @Test
    void 종료상태_일정_시간_이후_대기상태가_된다() {
        // given
        final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:00:03.000000Z"), TIME_ZONE);
        final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:03.000000Z"), TIME_ZONE);
        final Clock nightTime = Clock.fixed(Instant.parse("2024-01-01T00:01:13.000000Z"), TIME_ZONE);
        final Clock endTime = Clock.fixed(Instant.parse("2024-01-01T00:01:53.000000Z"), TIME_ZONE);
        final Clock endEndTime = Clock.fixed(Instant.parse("2024-01-01T00:02:52.000000Z"), TIME_ZONE);
        final Clock waitTime = Clock.fixed(Instant.parse("2024-01-01T00:02:53.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayTime);
        room.getStatusType(voteTime);
        room.getStatusType(nightTime);
        room.getPlayer("A").kill();
        room.getPlayer("B").kill();
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
