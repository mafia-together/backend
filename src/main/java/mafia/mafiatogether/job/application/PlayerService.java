package mafia.mafiatogether.job.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.response.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.Skill;
import mafia.mafiatogether.job.domain.SkillRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    private final SkillRepository skillRepository;
    private final PlayerJobRepository playerJobRepository;

    public JobResponse getPlayerJob(final String code, final String name) {
        final PlayerJob playerJob = playerJobRepository.findByCodeAndName(code, name)
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
        return new JobResponse(playerJob.getJob().getJobType().name());
    }

    public PlayerExecuteAbilityResponse executeSkill(
            final String code,
            final String name,
            final PlayerExecuteAbilityRequest request
    ) {
        final Skill skill = skillRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final List<PlayerJob> playerJobs = playerJobRepository.findByCode(code);
        final PlayerJob playerJob = playerJobs.stream()
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
        validateTarget(playerJobs, request.target());
        final String result = playerJob.getJob().applySkill(skill.getJobTargets(), playerJobs, request.target());
        final JobTarget jobTarget = new JobTarget(code, playerJob.getJob(), result);
        skill.addJobTarget(jobTarget);
        skillRepository.save(skill);
        return new PlayerExecuteAbilityResponse(playerJob.getJob().getJobType().name(), result);
    }

    private void validateTarget(final List<PlayerJob> playerJobs, String target) {
        if (target.isEmpty()) {
            return;
        }
        playerJobs.stream()
                .filter(value -> value.getName().equals(target))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
    }

    public MafiaTargetResponse getTarget(
            final String code,
            final String name
    ) {
        final PlayerJob playerJob = playerJobRepository.findByCodeAndName(code, name)
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
        final Skill skill = skillRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final JobTarget mafiaJobTarget = skill.findJobTargetBy(JobType.MAFIA);
        return new MafiaTargetResponse(mafiaJobTarget.getTarget());
    }

    public RoomNightResultResponse findJobResult(final String code) {
        final Skill skill = skillRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_PLAYER));
        final String target = skill.findTarget();
        return new RoomNightResultResponse(target);
    }
}
