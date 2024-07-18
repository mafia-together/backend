package mafia.mafiatogether.room.domain.status;

import java.time.Clock;
import java.util.List;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.job.domain.Mafia;
import mafia.mafiatogether.room.domain.status.EndStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class EndStatusTest {

    @Test
    void 시민_승리_결과_반환() {
        Mafia mafia = new Mafia();
        Player A = Player.create("A");
        A.modifyJob(mafia);
        A.kill();
        Player B = Player.create("B");
        B.modifyJob(mafia);
        B.kill();
        Player C = Player.create("C");
        Player D = Player.create("D");
        Player E = Player.create("E");

        List<Player> players = List.of(A, B, C, D, E);

        EndStatus endStatus = EndStatus.create(players, Clock.systemUTC().millis());

        Assertions.assertThat(endStatus.getWinnerJob()).isEqualTo(JobType.CITIZEN);
        Assertions.assertThat(endStatus.getWinner()).containsAll(List.of(C, D, E));
        Assertions.assertThat(endStatus.getLoser()).containsAll(List.of(A, B));
    }

    @Test
    void 마피아_승리_결과_반환() {
        Mafia mafia = new Mafia();
        Player A = Player.create("A");
        A.modifyJob(mafia);
        Player B = Player.create("B");
        B.modifyJob(mafia);
        Player C = Player.create("C");
        C.kill();
        Player D = Player.create("D");
        Player E = Player.create("E");

        List<Player> players = List.of(A, B, C, D, E);

        EndStatus endStatus = EndStatus.create(players, Clock.systemUTC().millis());

        Assertions.assertThat(endStatus.getWinnerJob()).isEqualTo(JobType.MAFIA);
        Assertions.assertThat(endStatus.getLoser()).containsAll(List.of(C, D, E));
        Assertions.assertThat(endStatus.getWinner()).containsAll(List.of(A, B));
    }
}
