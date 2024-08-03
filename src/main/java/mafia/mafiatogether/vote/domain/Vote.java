package mafia.mafiatogether.vote.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private String code;
    private String name;
    private String target;

    @Id
    @JsonIgnore
    public String getId() {
        return code + ":" + name;
    }

    public static String countVotes(final List<Vote> votes) {
        final Map<String, Integer> voteCounts = countTargetVotes(votes);
        final int maxCount = voteCounts.values().stream().max(Integer::compareTo).orElse(0);
        final List<String> maxCounts = voteCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();
        if (maxCounts.size() == 1) {
            return maxCounts.get(0);
        }
        return "";
    }

    private static Map<String, Integer> countTargetVotes(List<Vote> votes) {
        final Map<String, Integer> targetCounts = new HashMap<>();
        for (Vote vote : votes) {
            targetCounts.put(vote.target, targetCounts.getOrDefault(vote.target, 0) + 1);
        }
        return targetCounts;
    }
}
