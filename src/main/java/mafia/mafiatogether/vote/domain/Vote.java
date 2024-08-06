package mafia.mafiatogether.vote.domain;

import java.util.ArrayList;
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
    private List<VoteTarget> voteTargets;

    public Vote() {
        this.voteTargets = new ArrayList<>();
    }

    public void addVoteTarget(VoteTarget voteTarget) {
        this.voteTargets.add(voteTarget);
    }

    public void clearVoteTargets() {
        voteTargets.clear();
    }

    public String countVotes() {
        final Map<String, Integer> voteCounts = countTargetVotes(voteTargets);
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

    private Map<String, Integer> countTargetVotes(List<VoteTarget> voteTargets) {
        final Map<String, Integer> targetCounts = new HashMap<>();
        for (VoteTarget voteTarget : voteTargets) {
            targetCounts.put(voteTarget.getTarget(), targetCounts.getOrDefault(voteTarget.getTarget(), 0) + 1);
        }
        return targetCounts;
    }

    public int getVotedCount() {
        return voteTargets.size();
    }
}
