package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class SkillTest {

    @Test
    void 마피아_처형대상을_처형할_수_있다() {
        // given
        final String expect = "target";
        final Skill skill = new Skill("testCode", new ArrayList<>());
        final JobTarget mafiaTarget = new JobTarget("testCode", new Mafia(), expect);
        skill.addJobTarget(mafiaTarget);

        // when
        final String actual = skill.findTarget();

        // then
        Assertions.assertThat(actual).isEqualTo(expect);
    }

    @Test
    void 마피아_처형대상을_의사가_치료할_수_있다() {
        // given
        final String target = "target";
        final Skill skill = new Skill("testCode", new ArrayList<>());
        final JobTarget mafiaTarget = new JobTarget("testCode", new Mafia(), target);
        final JobTarget doctorTarget = new JobTarget("testCode", new Doctor(), target);
        skill.addJobTarget(doctorTarget);
        skill.addJobTarget(mafiaTarget);
        // when
        final String expect = skill.findTarget();

        // then
        Assertions.assertThat(expect).isBlank();
    }
}
