package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import java.util.Map;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Doctor implements Job {

    @Override
    public String applySkill(
            final Map<JobType, String> jobTargets,
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
