package mafia.mafiatogether.job.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.job.domain.jobtype.Citizen;
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
    private List<PlayerJob> PLAYER_JOBS = List.of(
            new PlayerJob(CODE, MAFIA, new Mafia()),
            new PlayerJob(CODE, CITIZEN, new Citizen()),
            new PlayerJob(CODE, "police", new Police())
    );

    @Test
    void 이미_스킬을_사용한_경우_예외가_발생한다() {
        // given
        final List<JobTarget> jobTargets = List.of(new JobTarget(CODE, new Police(), MAFIA));
        final Police police = new Police();

        // when & then
        assertThatThrownBy(() -> police.applySkill(jobTargets, PLAYER_JOBS, MAFIA))
                .isInstanceOf(PlayerException.class)
                .hasMessage("이미 스킬을 사용했습니다.");
    }

    @Test
    void 경찰은_마피아를_확인할_수_있다() {
        //given
        final List<JobTarget> jobTargets = new ArrayList<>();
        final Police police = new Police();

        // when
        String actual = police.applySkill(jobTargets, PLAYER_JOBS, MAFIA);

        //then
        Assertions.assertThat(actual).isEqualTo(JobType.MAFIA.name());
    }

    @Test
    void 경찰은_시민을_확인할_수_있다() {
        //given
        final List<JobTarget> jobTargets = new ArrayList<>();
        Police police = new Police();

        // when
        String actual = police.applySkill(jobTargets, PLAYER_JOBS, CITIZEN);

        //then
        Assertions.assertThat(actual).isEqualTo(JobType.CITIZEN.name());
    }
}
