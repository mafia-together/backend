package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Citizen implements Job {

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        throw new PlayerException(ExceptionCode.INVALID_CITIZEN_SKILL);
    }

    @Override
    public JobType getJobType() {
        return JobType.CITIZEN;
    }
}
