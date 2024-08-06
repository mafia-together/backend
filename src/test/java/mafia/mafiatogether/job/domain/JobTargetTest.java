package mafia.mafiatogether.job.domain;

import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class JobTargetTest {

    @Test
    void 마피아_처형대상을_처형할_수_있다() {
        // given
        final String expect = "target";
        final JobTarget jobTarget = new JobTarget();
        jobTarget.addJobTarget(JobType.MAFIA, expect);

        // when
        final String actual = jobTarget.findTarget();

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 마피아_처형대상을_의사가_치료할_수_있다() {
        // given
        final String target = "target";
        final JobTarget jobTarget = new JobTarget();
        jobTarget.addJobTarget(JobType.DOCTOR, target);
        jobTarget.addJobTarget(JobType.MAFIA, target);
        // when
        final String expect = jobTarget.findTarget();

        // then
        Assertions.assertThat(expect).isBlank();
    }
}
