package mafia.mafiatogether.job.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("skill")
@AllArgsConstructor
public class JobTarget {

    @Id
    private String code;
    private Map<JobType, String> jobTargets;

    public JobTarget() {
        this.jobTargets = new HashMap<>();
    }


    public String findTarget() {
        final String mafiaTarget = findJobTargetBy(JobType.MAFIA);
        final String doctorTarget = findJobTargetBy(JobType.DOCTOR);
        if (mafiaTarget.equals(doctorTarget)) {
            return null;
        }
        return mafiaTarget;
    }

    public String findJobTargetBy(final JobType jobType) {
        return jobTargets.getOrDefault(jobType, null);
    }

    public void clearJobTargets() {
        jobTargets.clear();
    }

    public void addJobTarget(final JobType jobType, final String target) {
        this.jobTargets.put(jobType, target);
    }
}
