package mafia.mafiatogether.job.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.job.domain.jobtype.Job;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobTarget {

    private String code;
    private Job job;
    private String target;

    public static final JobTarget NONE = new JobTarget(null, null, null);
}
