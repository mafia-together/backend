package mafia.mafiatogether.job.domain;

import java.util.List;
import java.util.Optional;

public interface JobTargetRepository {
    List<JobTarget> findAllByCode(String code);

    void save(JobTarget jobTarget);

    Optional<JobTarget> findByCodeAndName(String code, String name);
}
