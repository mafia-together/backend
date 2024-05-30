package mafia.mafiatogether.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Vote {

    private final Map<Player, Player> playerVote;
    private Player votedPlayer;

    protected static Vote create() {
        return new Vote(new ConcurrentHashMap<>(), Player.NONE);
    }

    public void choose(final Player player, final Player target) {
        playerVote.put(player, target);
    }

    public String getVoteResult() {
        return votedPlayer.getName();
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
        Player maxVotedPlayer = Player.NONE;
        for (Entry<Player, Integer> voteCount : voteCounts.entrySet()) {
            if (maxCount == voteCount.getValue()) {
                count++;
            }
            if (maxCount < voteCount.getValue()) {
                maxCount = voteCount.getValue();
                count = 1;
                maxVotedPlayer = voteCount.getKey();
            }
        }
        if (count > 1) {
            return Player.NONE;
        }
        return maxVotedPlayer;
    }

    public void executeVote() {
        votedPlayer = countVotes();
        votedPlayer.kill();
    }

    public void clear() {
        votedPlayer = Player.NONE;
        playerVote.clear();
    }

    public boolean isAllParticipatedVote(final Long total) {
        return this.playerVote.keySet().size() == total;
    }
}
