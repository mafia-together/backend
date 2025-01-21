package mafia.mafiatogether.vote.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("vote")
@AllArgsConstructor
public class Vote {

    @Id
    private String code;
    private Map<String, String> voteTargets;

    public Vote() {
        this.voteTargets = new HashMap<>();
    }

    public void addVoteTarget(final String name, String vote) {
        this.voteTargets.put(name, vote);
    }

    public void clearVoteTargets() {
        voteTargets.clear();
    }

    public String countVotes() {
        final Map<String, Integer> voteCounts = countTargetVotes();
        final int maxCount = voteCounts.values().stream().max(Integer::compareTo).orElse(0);
        final List<String> maxCounts = voteCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();
        if (maxCounts.size() == 1) {
            return maxCounts.getFirst();
        }
        return "";
    }

    private Map<String, Integer> countTargetVotes() {
        final Map<String, Integer> targetCounts = new HashMap<>();
        for (String target : voteTargets.values()) {
            targetCounts.put(target, targetCounts.getOrDefault(target, 0) + 1);
        }
        return targetCounts;
    }

    public int getVotedCount() {
        return voteTargets.size();
    }
}
