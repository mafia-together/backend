package mafia.mafiatogether.job.domain;

import java.util.List;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class JobTargetTest {

    @Test
    void 마피아_처형대상을_처형할_수_있다() {
        // given
        final String expect = "target";
        final JobTarget mafiaTarget = new JobTarget("testCode", new Mafia(), expect);

        // when
        final String actual = JobTarget.findTarget(List.of(mafiaTarget));

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 마피아_처형대상을_의사가_치료할_수_있다() {
        // given
        final String target = "target";
        final JobTarget mafiaTarget = new JobTarget("testCode", new Mafia(), target);
        final JobTarget doctorTarget = new JobTarget("testCode", new Doctor(), target);

        // when
        final String expect = JobTarget.findTarget(List.of(doctorTarget, mafiaTarget));

        // then
        Assertions.assertThat(expect).isBlank();
    }
}
