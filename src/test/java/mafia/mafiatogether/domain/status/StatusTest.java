package mafia.mafiatogether.domain.status;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class StatusTest {

    private static final ZoneId TIME_ZONE = ZoneId.of("UTC");
    private static final Clock roomCreatedTime = Clock.fixed(Instant.parse("2024-01-01T00:00:00.000000Z"), TIME_ZONE);

    private Room room;
    private Player a = Player.create("A");
    private Player b = Player.create("B");
    private Player c = Player.create("C");

    @BeforeEach
    void setRoom() {
        room = Room.create(new RoomInfo(5, 2, 0, 1), roomCreatedTime);
        a = Player.create("A");
        b = Player.create("B");
        c = Player.create("C");
        Player d = Player.create("D");
        Player e = Player.create("E");

        room.joinPlayer(a);
        room.joinPlayer(b);
        room.joinPlayer(c);
        room.joinPlayer(d);
        room.joinPlayer(e);
    }

    @Test
    void 일정시간_이후_낮이_끝난다() {
        // given
        room.modifyStatus(StatusType.DAY, roomCreatedTime);

        // when & then
        assertSoftly(
                softly -> {
                    final Clock dayTime = Clock.fixed(Instant.parse("2024-01-01T00:01:40.000000Z"), TIME_ZONE);
                    softly.assertThat(room.getStatusType(dayTime)).isEqualTo(StatusType.DAY);
                    final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:41.000000Z"), TIME_ZONE);
                    softly.assertThat(room.getStatusType(dayEndTime)).isEqualTo(StatusType.VOTE);
                }
        );
    }

    @Test
    void 일정시간_이후_투표시간이_끝난다() {
        // given
        final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:41.000000Z"), TIME_ZONE);
        final Clock voteEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:52.000000Z"), TIME_ZONE);
        final Clock voteTime = Clock.fixed(Instant.parse("2024-01-01T00:01:51.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayEndTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(voteTime)).isEqualTo(StatusType.VOTE);
                    softly.assertThat(room.getStatusType(voteEndTime)).isEqualTo(StatusType.NIGHT);
                }
        );
    }

    @Test
    void 일정시간_이후_밤이_끝난다() {
        // given
        final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:41.000000Z"), TIME_ZONE);
        final Clock voteEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:52.000000Z"), TIME_ZONE);
        final Clock nightTime = Clock.fixed(Instant.parse("2024-01-01T00:02:32.000000Z"), TIME_ZONE);
        final Clock nightEndTime = Clock.fixed(Instant.parse("2024-01-01T00:02:33.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayEndTime);
        room.getStatusType(voteEndTime);

        // when & then
        assertSoftly(
                softly -> {
                    softly.assertThat(room.getStatusType(nightTime)).isEqualTo(StatusType.NIGHT);
                    softly.assertThat(room.getStatusType(nightEndTime)).isEqualTo(StatusType.DAY);
                }
        );
    }

    @Test
    void 투표결과_게임종료_조건달성시_게임이_종료된다() {
        // given
        final Clock dayEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:41.000000Z"), TIME_ZONE);
        final Clock voteEndTime = Clock.fixed(Instant.parse("2024-01-01T00:01:52.000000Z"), TIME_ZONE);
        final Clock nightEndTime = Clock.fixed(Instant.parse("2024-01-01T00:02:33.000000Z"), TIME_ZONE);

        room.modifyStatus(StatusType.DAY, roomCreatedTime);
        room.getStatusType(dayEndTime);
        room.getStatusType(voteEndTime);
        a.execute();
        b.execute();
        c.execute();

        // when & then
        assertEquals(StatusType.END, room.getStatusType(nightEndTime));
    }
}
