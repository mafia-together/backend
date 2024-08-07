package mafia.mafiatogether.job.domain.jobtype;

import java.util.Map;
import lombok.Getter;

@Getter
public class Mafia implements Job {

    @Override
    public String applySkill(
            final Map<JobType, String> jobTargets,
            final Map<String, Job> playerJobs,
            final String targetName
    ) {
        return targetName;
    }

    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }
}
