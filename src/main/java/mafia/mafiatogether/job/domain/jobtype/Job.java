package mafia.mafiatogether.job.domain.jobtype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface Job {

    String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    );

    JobType getJobType();
}
