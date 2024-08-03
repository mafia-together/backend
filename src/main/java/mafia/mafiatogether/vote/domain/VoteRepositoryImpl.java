package mafia.mafiatogether.vote.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Getter
@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final List<Vote> votes = new ArrayList<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "vote:";

    @Override
    public Vote save(Vote vote) {
        final String key = KEY_PREFIX + vote.getId();
        redisTemplate.opsForValue().set(key, vote);
        return vote;
    }

    @Override
    public List<Vote> findAllByCode(String code) {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        return keys.stream()
                .map(key -> (Vote) redisTemplate.opsForValue().get(key))
                .toList();
    }

    @Override
    public void deleteAllByCode(String code) {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + code + ":*");
        redisTemplate.delete(keys);
    }
}
