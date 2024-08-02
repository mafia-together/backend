package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<JobTarget> optionalJobTarget = jobTargets.stream().
                filter(value -> value.getCode().equals(jobTarget.getCode()) && value.getName().equals(jobTarget.getName()))
                .findFirst();
        if (optionalJobTarget.isPresent()) {
            jobTargets.remove(optionalJobTarget.get());
        }
        jobTargets.add(jobTarget);
    }

    @Override
    public Optional<JobTarget> findByCodeAndName(String code, String name) {
        return jobTargets.stream()
                .filter(jobTarget -> jobTarget.getCode().equals(code) && jobTarget.getName().equals(name))
                .findFirst();
    }
}
