package mafia.mafiatogether.job.application;

import static org.mockito.BDDMockito.given;

import java.util.HashMap;
import java.util.Optional;
import mafia.mafiatogether.job.application.dto.request.JobExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobExecuteAbilityResponse;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.Citizen;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import mafia.mafiatogether.job.domain.jobtype.Police;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class JobServiceTest {

    @Autowired
    private JobService jobService;

    @MockBean
    private JobTargetRepository jobTargetRepository;
    @MockBean
    private PlayerJobRepository playerJobRepository;

    @Test
    void 마피아_능력을_사용할_수_있다() {
        //given
        final String code = "test";
        given(jobTargetRepository.findById(code)).willReturn(Optional.of(new JobTarget(code, new HashMap<>())));
        given(playerJobRepository.findById(code)).willReturn(Optional.of(new PlayerJob(code, new HashMap<>() {{
            put("파워", new Mafia());
            put("달리", new Citizen());
            put("매튜", new Doctor());
            put("지윤", new Police());
        }})));

        //when
        JobExecuteAbilityResponse response = jobService.executeSkill(
                code,
                "파워",
                new JobExecuteAbilityRequest("매튜")
        );

        //then
        Assertions.assertThat(response.job()).isEqualTo("MAFIA");
        Assertions.assertThat(response.result()).isEqualTo("매튜");
    }

    @Test
    void 경찰_능력을_사용할_수_있다() {
        //given
        final String code = "test";
        given(jobTargetRepository.findById(code)).willReturn(Optional.of(new JobTarget(code, new HashMap<>())));
        given(playerJobRepository.findById(code)).willReturn(Optional.of(new PlayerJob(code, new HashMap<>() {{
            put("파워", new Mafia());
            put("달리", new Citizen());
            put("매튜", new Doctor());
            put("지윤", new Police());
        }})));

        //when
        JobExecuteAbilityResponse response = jobService.executeSkill(
                code,
                "지윤",
                new JobExecuteAbilityRequest("파워")
        );

        //then
        Assertions.assertThat(response.job()).isEqualTo("POLICE");
        Assertions.assertThat(response.result()).isEqualTo("MAFIA");

    }
}
