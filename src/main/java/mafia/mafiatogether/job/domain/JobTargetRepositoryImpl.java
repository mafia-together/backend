package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.stereotype.Repository;

@Repository
public class JobTargetRepositoryImpl implements JobTargetRepository {

    private final List<JobTarget> jobTargets = new ArrayList<>();

    @Override
    public List<JobTarget> findAllByCode(String code) {
        return jobTargets.stream().
                filter(jobTarget -> jobTarget.getCode().equals(code))
                .toList();
    }

    @Override
    public void save(JobTarget jobTarget) {
        final JobType jobType = jobTarget.getJob().getJobType();
        Optional<JobTarget> optionalJobTarget = jobTargets.stream()
                .filter(value -> value.getCode().equals(jobTarget.getCode()) && value.isSameJobType(jobType))
                .findFirst();
        if (optionalJobTarget.isPresent()) {
            jobTargets.remove(optionalJobTarget.get());
        }
        jobTargets.add(jobTarget);
    }

    @Override
    public Optional<JobTarget> findByCodeAndJobType(String code, JobType jobType) {
        return jobTargets.stream()
                .filter(jobTarget -> jobTarget.isSameJobType(jobType))
                .findFirst();
    }

    @Override
    public void deleteAllByCode(String code) {
        List<JobTarget> deleteJobTarget = new ArrayList<>();
        for (JobTarget jobTarget : jobTargets) {
            if (jobTarget.getCode().equals(code)) {
                deleteJobTarget.add(jobTarget);
            }
        }
        for (JobTarget jobTarget : deleteJobTarget) {
            jobTargets.remove(jobTarget);
        }
    }
}
