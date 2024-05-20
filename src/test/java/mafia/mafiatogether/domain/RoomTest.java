package mafia.mafiatogether.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import mafia.mafiatogether.domain.job.JobType;
import mafia.mafiatogether.domain.status.StatusType;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class RoomTest {

    @Test
    void 직업을_배정할_수_있다() {
        //given
        Room room = Room.create(new RoomInfo(5, 2, 0, 1), Clock.systemDefaultZone().millis());

        room.joinPlayer("A");
        room.joinPlayer("B");
        room.joinPlayer("C");
        room.joinPlayer("D");
        room.joinPlayer("E");

        Player a = room.getPlayer("A");
        Player b = room.getPlayer("B");
        Player c = room.getPlayer("C");
        Player d = room.getPlayer("D");
        Player e = room.getPlayer("E");

        //when
        room.modifyStatus(StatusType.NIGHT, Clock.systemDefaultZone().millis());

        //then

        List<JobType> jobTypes = new ArrayList<>();
        jobTypes.add(a.getJobType());
        jobTypes.add(b.getJobType());
        jobTypes.add(c.getJobType());
        jobTypes.add(d.getJobType());
        jobTypes.add(e.getJobType());

        assertEquals(5, jobTypes.size());
        assertTrue(jobTypes.contains(JobType.CITIZEN));
        assertTrue(jobTypes.contains(JobType.MAFIA));
        assertTrue(jobTypes.contains(JobType.POLICE));

        int mafiaPlayers = 0;
        for (JobType symbol : jobTypes) {
            if (symbol == JobType.MAFIA) {
                mafiaPlayers++;
            }
        }

        assertEquals(2, mafiaPlayers);
    }

    @Test
    void 투표를_할_수_있다() {
        // given
        final Room room = Room.create(new RoomInfo(5, 3, 0, 1), Clock.systemDefaultZone().millis());
        room.joinPlayer("A");
        room.joinPlayer("B");
        room.joinPlayer("C");
        room.joinPlayer("D");
        room.joinPlayer("E");

        Player a = room.getPlayer("A");
        Player b = room.getPlayer("B");
        Player c = room.getPlayer("C");
        Player d = room.getPlayer("D");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());

        // when
        room.votePlayer(a.getName(), b.getName());
        room.votePlayer(a.getName(), b.getName());
        room.votePlayer(b.getName(), d.getName());
        room.votePlayer(c.getName(), d.getName());
        room.executeVote();

        // then
        assertEquals(room.getVoteResult(), d.getName());
    }

    @Test
    void 동표일떄_투표가_무효가_된다() {
        // given
        final Room room = Room.create(new RoomInfo(5, 3, 0, 1), Clock.systemDefaultZone().millis());
        room.joinPlayer("A");
        room.joinPlayer("B");
        room.joinPlayer("C");
        room.joinPlayer("D");
        room.joinPlayer("E");

        Player a = room.getPlayer("A");
        Player b = room.getPlayer("B");
        Player c = room.getPlayer("C");
        Player d = room.getPlayer("D");
        Player e = room.getPlayer("E");

        room.modifyStatus(StatusType.DAY, Clock.systemDefaultZone().millis());

        // when
        room.votePlayer(a.getName(), c.getName());
        room.votePlayer(b.getName(), c.getName());
        room.votePlayer(c.getName(), e.getName());
        room.votePlayer(d.getName(), e.getName());

        // then
        assertNull(room.getVoteResult());
    }
}
