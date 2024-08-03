package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerJobRepositoryImpl implements PlayerJobRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "player_job:";

    @Override
    public Optional<PlayerJob> findByCodeAndName(final String code, String name) {
        return Optional.ofNullable((PlayerJob) redisTemplate.opsForValue().get(KEY_PREFIX + code + ":" + name));
    }

    @Override
    public List<PlayerJob> findByCode(final String code) {
        final Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }
        return keys.stream()
                .map(key -> (PlayerJob) redisTemplate.opsForValue().get(key))
                .toList();
    }

    @Override
    public PlayerJob save(final PlayerJob playerJob) {
        final String key = KEY_PREFIX + playerJob.getId();
        redisTemplate.opsForValue().set(key, playerJob);
        return playerJob;
    }

    @Override
    public void deleteAllByCode(final String code) {
        final Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }
}
