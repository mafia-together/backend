package mafia.mafiatogether.room.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.job.domain.Mafia;
import mafia.mafiatogether.room.domain.status.StatusType;
import mafia.mafiatogether.job.domain.Player;
import org.assertj.core.api.Assertions;
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
        room.votePlayer(a.getName(), b.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(a.getName(), b.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(b.getName(), d.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(c.getName(), d.getName(), Clock.systemDefaultZone().millis());
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
        room.votePlayer(a.getName(), c.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(b.getName(), c.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(c.getName(), e.getName(), Clock.systemDefaultZone().millis());
        room.votePlayer(d.getName(), e.getName(), Clock.systemDefaultZone().millis());

        // then
        Assertions.assertThat(room.getVoteResult()).isBlank();
    }

    @Test
    void 초기_마피아_타겟은_null이다() {
        // given
        final Room room = Room.create(new RoomInfo(3, 1, 0, 0), Clock.systemDefaultZone().millis());
        room.joinPlayer("A");
        room.joinPlayer("B");
        room.joinPlayer("C");
        Player mafia = room.getPlayer("A");
        mafia.modifyJob(new Mafia());

        // when & then
        Assertions.assertThat(room.getJobsTarget("A")).isNull();
    }
}
