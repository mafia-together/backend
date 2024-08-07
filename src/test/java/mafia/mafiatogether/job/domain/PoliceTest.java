package mafia.mafiatogether.job.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.job.domain.jobtype.Citizen;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import mafia.mafiatogether.job.domain.jobtype.Police;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class PoliceTest {

    private static final String CODE = "1234567890";
    private static final String MAFIA = "mafia";
    private static final String CITIZEN = "citizen";
    private static final String POLICE = "police";
    private final Map<String, Job> PLAYER_JOB = Map.of(
            MAFIA, new Mafia(),
            CITIZEN, new Citizen(),
            POLICE, new Police()
    );

    @Test
    void 이미_스킬을_사용한_경우_예외가_발생한다() {
        // given
        final Map<JobType, String> jobTargets = Map.of(JobType.POLICE, MAFIA);
        final Police police = new Police();

        // when & then
        assertThatThrownBy(() -> police.applySkill(jobTargets, PLAYER_JOB, MAFIA))
                .isInstanceOf(PlayerException.class)
                .hasMessage("이미 스킬을 사용했습니다.");
    }

    @Test
    void 경찰은_마피아를_확인할_수_있다() {
        //given
        final Map<JobType, String> jobTargets = Map.of();
        final Police police = new Police();

        // when
        String actual = police.applySkill(jobTargets, PLAYER_JOB, MAFIA);

        //then
        Assertions.assertThat(actual).isEqualTo(JobType.MAFIA.name());
    }

    @Test
    void 경찰은_시민을_확인할_수_있다() {
        //given
        final Map<JobType, String> jobTargets = Map.of();
        Police police = new Police();

        // when
        String actual = police.applySkill(jobTargets, PLAYER_JOB, CITIZEN);

        //then
        Assertions.assertThat(actual).isEqualTo(JobType.CITIZEN.name());
    }
}
