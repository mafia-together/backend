package mafia.mafiatogether.job.domain.jobtype;

import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;

public class Police implements Job {

    @Override
    public String applySkill(
            final Map<JobType, String> jobTargets,
            final Map<String, Job> playerJobs,
            final String targetName
    ) {

        if (jobTargets.containsKey(JobType.POLICE)) {
            throw new PlayerException(ExceptionCode.POLICE_DUPLICATE_SKILL);
        }
        JobType jobType = playerJobs.entrySet().stream()
                .filter(playerJob -> playerJob.getKey().equals(targetName))
                .findFirst()
                .orElseThrow(() -> new PlayerException(ExceptionCode.INVALID_PLAYER))
                .getValue()
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
