package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Doctor implements Job {

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
        return JobType.DOCTOR;
    }
}
