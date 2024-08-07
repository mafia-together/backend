package mafia.mafiatogether.job.domain.jobtype;

import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;

public class Citizen implements Job {

    @Override
    public String applySkill(
            final Map<JobType, String> jobTargets,
            final Map<String, Job> playerJobs,
            final String targetName
    ) {
        throw new PlayerException(ExceptionCode.INVALID_CITIZEN_SKILL);
    }

    @Override
    public JobType getJobType() {
        return JobType.CITIZEN;
    }
}
