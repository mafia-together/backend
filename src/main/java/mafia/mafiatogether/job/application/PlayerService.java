package mafia.mafiatogether.job.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.response.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.application.dto.response.RoomNightResultResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final JobTargetRepository jobTargetRepository;
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
        final List<JobTarget> jobTargets = jobTargetRepository.findAllByCode(code);
        final List<PlayerJob> playerJobs = playerJobRepository.findByCode(code);
        final PlayerJob playerJob = playerJobs.stream()
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER));
        validateTarget(playerJobs, request.target());
        final String result = playerJob.getJob().applySkill(jobTargets, playerJobs, request.target());
        final JobTarget jobTarget = new JobTarget(code, playerJob.getJob(), result);
        jobTargetRepository.save(jobTarget);
        return new PlayerExecuteAbilityResponse(playerJob.getJob().getJobType().name(), result);
    }

    private void validateTarget(List<PlayerJob> playerJobs, String target) {
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
        final JobTarget mafiaJobTarget = jobTargetRepository.findByCodeAndJobType(code, playerJob.getJob().getJobType())
                .orElse(JobTarget.NONE);
        return new MafiaTargetResponse(mafiaJobTarget.getTarget());
    }

    public RoomNightResultResponse findJobResult(String code) {
        final List<JobTarget> jobTargets = jobTargetRepository.findAllByCode(code);
        final String target = JobTarget.findTarget(jobTargets);
        return new RoomNightResultResponse(target);
    }
}
