package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import lombok.Getter;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

@Getter
public class Mafia implements Job {

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        return targetName;
    }

    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }
}
