package mafia.mafiatogether.job.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.JobType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobTarget {

    private String code;
    private Job job;
    private String target;

    @JsonIgnore
    public String getId() {
        return code + ":" + job.getJobType();
    }

    public static final JobTarget NONE = new JobTarget(null, null, null);

    public static String findTarget(final List<JobTarget> jobTargets) {
        final JobTarget mafiaTarget = jobTargets.stream()
                .filter(jobTarget -> jobTarget.getJob().getJobType().equals(JobType.MAFIA))
                .findFirst()
                .orElse(NONE);
        final JobTarget doctorTarget = jobTargets.stream()
                .filter(jobTarget -> jobTarget.getJob().getJobType().equals(JobType.DOCTOR))
                .findFirst()
                .orElse(NONE);
        if (mafiaTarget.getTarget().equals(doctorTarget.getTarget())) {
            return NONE.getTarget();
        }
        return mafiaTarget.getTarget();
    }
}
