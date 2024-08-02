package mafia.mafiatogether.game.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class GameTest {

    @Test
    void 직업을_배정할_수_있다() {
        //given
        Room room = Room.create(new RoomInfo(5, 2, 0, 1));
        room.joinPlayer("A");
        room.joinPlayer("B");
        room.joinPlayer("C");
        room.joinPlayer("D");
        room.joinPlayer("E");
        Game game = Game.create(room, Clock.systemDefaultZone().millis());

        Player a = game.getPlayer("A");
        Player b = game.getPlayer("B");
        Player c = game.getPlayer("C");
        Player d = game.getPlayer("D");
        Player e = game.getPlayer("E");

        //when
        game.distributeRole();

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
}
