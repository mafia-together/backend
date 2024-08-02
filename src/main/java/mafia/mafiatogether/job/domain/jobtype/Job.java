package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

public interface Job {

    String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    );

    JobType getJobType();
}
