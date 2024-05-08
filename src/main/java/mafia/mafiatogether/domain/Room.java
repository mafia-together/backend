package mafia.mafiatogether.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.job.Job;
import mafia.mafiatogether.domain.job.JobTarget;
import mafia.mafiatogether.domain.job.JobType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final Map<String, Player> players;
    private final Map<Player, Player> votes;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;
    private Player votedPlayer;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(),
                Status.WAIT,
                roomInfo,
                Chat.chat(),
                new JobTarget(),
                Player.NONE
        );
    }

    public void modifyStatus(final Status status) {
        if (this.status.equals(Status.WAIT)) {
            distributeRole();
        }
        this.status = status;
    }

    public void joinPlayer(final Player player) {
        players.put(player.getName(), player);
    }

    private void distributeRole() {
        final Queue<Job> jobs = roomInfo.getRandomJobQueue();
        for (Player player : players.values()) {
            if (jobs.isEmpty()) {
                break;
            }
            player.modifyRole(jobs.poll());
        }
    }

    public Player getPlayer(final String name) {
        if (!players.containsKey(name)) {
            throw new RoomException(ExceptionCode.INVALID_NAMES);
        }
        return players.get(name);
    }

    public String executeAbility(final String name, final Player target) {
        final Player player = players.get(name);
        return player.getJob().executeAbility(target, jobTarget);
    }

    public String getJobsTarget(final String name) {
        final Player player = players.get(name);
        final JobType jobType = player.getRoleSymbol();
        return jobTarget.getTarget(jobType).getName();
    }

    public void votePlayer(final String name, final String targetName) {
        final Player player = players.get(name);
        final Player target = players.get(targetName);
        votes.put(player, target);
    }

    public String getVoteResult() {
        return countVotes().getName();
    }

    private Player countVotes() {
        final Map<Player, Integer> voteCounts = new HashMap<>();
        for (final Player player : votes.values()) {
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
}
