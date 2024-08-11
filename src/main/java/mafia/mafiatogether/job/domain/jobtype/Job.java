package mafia.mafiatogether.job.domain.jobtype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface Job {

    String applySkill(
            final Map<JobType, String> jobTargets,
            final Map<String, Job> playerJobs,
            final String targetName
    );

    JobType getJobType();
}
