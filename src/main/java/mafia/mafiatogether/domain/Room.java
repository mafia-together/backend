package mafia.mafiatogether.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
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
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;
    private Player votedPlayer;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
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
        player.setVote(target);
    }

    // todo : 추후 day 종료 구현시 countVotes() 메서드 이동
    public String getVoteResult() {
        countVotes();
        return votedPlayer.getName();
    }

    private void countVotes() {
        final Map<Player, Integer> voteCount = new HashMap<>();
        for (final Player player : players.values()) {
            Optional.of(player.getVote()).ifPresent(
                    vote -> voteCount.put(vote, voteCount.getOrDefault(vote, 0) + 1)
            );
            player.clear();
        }
        final Optional<Entry<Player, Integer>> maxVotedPlayer = voteCount.entrySet().stream()
                .max(Comparator.comparingInt(Entry::getValue));
        maxVotedPlayer.ifPresent(entry -> setVotedPlayer(voteCount, entry));
    }

    private void setVotedPlayer(Map<Player, Integer> voteCount, Entry<Player, Integer> entry) {
        final long maxCount = voteCount.values().stream()
                .filter(i -> Objects.equals(i, entry.getValue()))
                .count();
        if (maxCount == 1) {
            votedPlayer = entry.getKey();
        }
    }
}
