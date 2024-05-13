package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.domain.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class JobTargetTest {

    private final Player player1 = Player.create("player1");
    private final Player player2 = Player.create("player2");

    @Test
    void 마피아_처형대상을_처형할_수_있다() {
        // given
        final JobTarget jobTarget = new JobTarget();
        jobTarget.addTarget(JobType.MAFIA, player1);
        jobTarget.addTarget(JobType.DOCTOR, player2);

        // when
        jobTarget.execute();

        // then
        Assertions.assertThat(player1.isAlive()).isFalse();
    }

    @Test
    void 마피아_처형대상을_의사가_치료할_수_있다() {
        // given
        final JobTarget jobTarget = new JobTarget();
        jobTarget.addTarget(JobType.MAFIA, player1);
        jobTarget.addTarget(JobType.DOCTOR, player1);

        // when
        jobTarget.execute();

        // then
        Assertions.assertThat(player1.isAlive()).isTrue();
    }
}
