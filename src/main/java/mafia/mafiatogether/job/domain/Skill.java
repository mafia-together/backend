package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("skill")
@AllArgsConstructor
public class Skill {

    @Id
    private String code;
    private List<JobTarget> jobTargets;

    public Skill() {
        this.jobTargets = new ArrayList<>();
    }


    public String findTarget() {
        final JobTarget mafiaTarget = findJobTargetBy(JobType.MAFIA);
        final JobTarget doctorTarget = findJobTargetBy(JobType.DOCTOR);
        if (mafiaTarget.getTarget().equals(doctorTarget.getTarget())) {
            return JobTarget.NONE.getTarget();
        }
        return mafiaTarget.getTarget();
    }

    public JobTarget findJobTargetBy(final JobType jobType) {
        return jobTargets.stream()
                .filter(jobTarget -> jobTarget.getJob().getJobType().equals(jobType))
                .findFirst()
                .orElse(JobTarget.NONE);
    }

    public void clearJobTargets(){
        jobTargets.clear();
    }

    public void addJobTarget(final JobTarget jobTarget) {
        this.jobTargets.add(jobTarget);
    }
}
