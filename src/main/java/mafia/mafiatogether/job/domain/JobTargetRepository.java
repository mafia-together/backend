package mafia.mafiatogether.job.domain;

import java.util.List;
import java.util.Optional;
import mafia.mafiatogether.job.domain.jobtype.JobType;

public interface JobTargetRepository {
    List<JobTarget> findAllByCode(String code);

    JobTarget save(JobTarget jobTarget);

    Optional<JobTarget> findByCodeAndJobType(String code, JobType jobType);

    void deleteAllByCode(String code);
}
