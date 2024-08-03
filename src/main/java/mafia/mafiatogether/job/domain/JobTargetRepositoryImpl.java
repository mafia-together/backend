package mafia.mafiatogether.job.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobTargetRepositoryImpl implements JobTargetRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static String KEY_PREFIX = "job_target:";

    @Override
    public List<JobTarget> findAllByCode(String code) {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        return keys.stream()
                .map(key -> (JobTarget) redisTemplate.opsForValue().get(key))
                .toList();
    }

    @Override
    public JobTarget save(JobTarget jobTarget) {
        final String key = KEY_PREFIX + jobTarget.getId();
        redisTemplate.opsForValue().set(key, jobTarget);
        return jobTarget;
    }

    @Override
    public Optional<JobTarget> findByCodeAndJobType(String code, JobType jobType) {
        JobTarget jobTarget = (JobTarget) redisTemplate.opsForValue().get(KEY_PREFIX + code + ":" + jobType.name());
        return Optional.ofNullable(jobTarget);
    }

    @Override
    public void deleteAllByCode(String code) {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        redisTemplate.delete(keys);
    }
}
