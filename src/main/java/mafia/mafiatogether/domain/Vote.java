package mafia.mafiatogether.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Vote {

    private final Map<Player, Player> playerVote;

    protected static Vote create() {
        return new Vote(new ConcurrentHashMap<>());
    }

    public void choose(final Player player, final Player target) {
        playerVote.put(player, target);
    }

    public String getVoteResult() {
        return countVotes().getName();
    }

    private Player countVotes() {
        final Map<Player, Integer> voteCounts = new HashMap<>();
        for (final Player player : playerVote.values()) {
            voteCounts.put(player, voteCounts.getOrDefault(player, 0) + 1);
        }
        return findMaxVotedPlayer(voteCounts);
    }

    private Player findMaxVotedPlayer(final Map<Player, Integer> voteCounts) {
        int maxCount = 0;
        int count = 0;
        Player votedPlayer = Player.NONE;
        for (Entry<Player, Integer> voteCount : voteCounts.entrySet()) {
            if (maxCount == voteCount.getValue()) {
                count++;
            }
            if (maxCount < voteCount.getValue()) {
                maxCount = voteCount.getValue();
                count = 1;
                votedPlayer = voteCount.getKey();
            }
        }
        if (count > 1) {
            return Player.NONE;
        }
        return votedPlayer;
    }

    public void executeVote() {
        final Player player = countVotes();
        player.execute();
        playerVote.clear();
    }
}
