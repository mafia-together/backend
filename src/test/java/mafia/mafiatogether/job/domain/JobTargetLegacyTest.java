package mafia.mafiatogether.job.domain;

import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class JobTargetLegacyTest {

    private final Player player1 = Player.create("player1");
    private final Player player2 = Player.create("player2");

    @Test
    void 마피아_처형대상을_처형할_수_있다() {
        // given
        final JobTargetLegacy jobTargetLegacy = new JobTargetLegacy();
        jobTargetLegacy.addTarget(JobType.MAFIA, player1);
        jobTargetLegacy.addTarget(JobType.DOCTOR, player2);

        // when
        jobTargetLegacy.execute();

        // then
        Assertions.assertThat(player1.isAlive()).isFalse();
    }

    @Test
    void 마피아_처형대상을_의사가_치료할_수_있다() {
        // given
        final JobTargetLegacy jobTargetLegacy = new JobTargetLegacy();
        jobTargetLegacy.addTarget(JobType.MAFIA, player1);
        jobTargetLegacy.addTarget(JobType.DOCTOR, player1);

        // when
        jobTargetLegacy.execute();

        // then
        Assertions.assertThat(player1.isAlive()).isTrue();
    }

    @Test
    void 투표_결과를_반환받을_수_있다() {
        // given
        final JobTargetLegacy jobTargetLegacy = new JobTargetLegacy();
        jobTargetLegacy.addTarget(JobType.MAFIA, player1);
        jobTargetLegacy.addTarget(JobType.DOCTOR, player1);

        // when
        jobTargetLegacy.execute();

        // then
        Assertions.assertThat(jobTargetLegacy.getResult()).isEqualTo(player1);
    }
}
