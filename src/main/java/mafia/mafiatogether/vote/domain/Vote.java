package mafia.mafiatogether.vote.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private String code;
    private String name;
    private String target;

    // todo : 메서드 줄이기
    public static String countVotes(final List<Vote> votes){
        final Map<String, Integer> targetCounts = new HashMap<>();
        for (Vote vote : votes) {
            targetCounts.put(vote.target, targetCounts.getOrDefault(vote.target, 0) + 1);
        }
        int maxCount = 0;
        int numberOfMax = 0;
        String maxTarget = "";
        for (Entry<String, Integer> targetCount : targetCounts.entrySet()) {
            if (targetCount.getValue() > maxCount) {
                maxCount = targetCount.getValue();
                numberOfMax = 1;
                maxTarget = targetCount.getKey();
                continue;
            }
            if (targetCount.getValue() == maxCount) {
                numberOfMax ++;
            }
        }
        if (numberOfMax > 1){
            return "";
        }
        return maxTarget;
    }
}
