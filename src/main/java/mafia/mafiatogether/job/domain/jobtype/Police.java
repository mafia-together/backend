package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Police implements Job {

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        jobTargets.stream()
                .filter(jobTarget -> jobTarget.getJob().getJobType().equals(JobType.POLICE))
                .findFirst()
                .ifPresent(jobTarget -> {
                    throw new PlayerException(ExceptionCode.POLICE_DUPLICATE_SKILL);
                });
        JobType jobType = playerJobs.stream()
                .filter(playerJob -> playerJob.getName().equals(targetName))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER))
                .getJob()
                .getJobType();
        if (jobType.equals(JobType.MAFIA)) {
            return JobType.MAFIA.name();
        }
        return JobType.CITIZEN.name();
    }

    @Override
    public JobType getJobType() {
        return JobType.POLICE;
    }
}
