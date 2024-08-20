package mafia.mafiatogether.job.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.config.exception.GameException;
import mafia.mafiatogether.job.application.dto.request.PlayerExecuteAbilityRequest;
import mafia.mafiatogether.job.application.dto.response.JobResponse;
import mafia.mafiatogether.job.application.dto.response.MafiaTargetResponse;
import mafia.mafiatogether.job.application.dto.response.PlayerExecuteAbilityResponse;
import mafia.mafiatogether.job.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetRepository;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.Job;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final JobTargetRepository jobTargetRepository;
    private final PlayerJobRepository playerJobRepository;

    @Transactional(readOnly = true)
    public JobResponse getPlayerJob(final String code, final String name) {
        final PlayerJob playerJob = playerJobRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new JobResponse(playerJob.findJobByName(name).getJobType().name());
    }

    @Transactional
    public PlayerExecuteAbilityResponse executeSkill(
            final String code,
            final String name,
            final PlayerExecuteAbilityRequest request
    ) {
        final JobTarget jobTarget = jobTargetRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final PlayerJob playerJob = playerJobRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        validateTarget(playerJob, request.target());
        final Job requestJob = playerJob.findJobByName(name);
        final String result = requestJob.applySkill(jobTarget.getJobTargets(), playerJob.getPlayerJobs(),
                request.target());
        jobTarget.addJobTarget(requestJob.getJobType(), request.target());
        jobTargetRepository.save(jobTarget);
        return new PlayerExecuteAbilityResponse(requestJob.getJobType().name(), result);
    }

    private void validateTarget(final PlayerJob playerJob, String target) {
        if (target.isEmpty()) {
            return;
        }
        playerJob.findJobByName(target);
    }

    @Transactional(readOnly = true)
    public MafiaTargetResponse getTarget(
            final String code,
            final String name
    ) {
        final Job playerJob = playerJobRepository.findById(code)
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER))
                .findJobByName(name);
        final JobTarget jobTarget = jobTargetRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final String mafiaJobTarget = jobTarget.findJobTargetBy(playerJob.getJobType());
        return new MafiaTargetResponse(mafiaJobTarget);
    }

    @Transactional(readOnly = true)
    public RoomNightResultResponse findJobResult(final String code) {
        final JobTarget jobTarget = jobTargetRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_PLAYER));
        final String target = jobTarget.findTarget();
        return new RoomNightResultResponse(target);
    }
}
