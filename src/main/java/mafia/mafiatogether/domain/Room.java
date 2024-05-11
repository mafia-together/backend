package mafia.mafiatogether.domain;

import java.time.Clock;
import java.util.Map;
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
import mafia.mafiatogether.domain.status.Status;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.domain.status.Wait;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final Map<String, Player> players;
    private final Vote vote;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;

    public static Room create(final RoomInfo roomInfo, final Clock clock) {
        return new Room(
                new ConcurrentHashMap<>(),
                Vote.create(),
                Wait.create(clock),
                roomInfo,
                Chat.chat(),
                new JobTarget()
        );
    }

    public void modifyStatus(final StatusType statusType, final Clock clock) {
        this.status = status.getNextStatus(this, clock);
    }

    public void joinPlayer(final Player player) {
        players.put(player.getName(), player);
    }

    public StatusType getStatusType(final Clock clock) {
        if (status.isTimeOver(clock)) {
            status = status.getNextStatus(this, clock);
        }
        return status.getType();
    }

    public void distributeRole() {
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
        return player.getJob().applySkill(target, jobTarget);
    }

    public String getJobsTarget(final String name) {
        final Player player = players.get(name);
        final JobType jobType = player.getRoleSymbol();
        return jobTarget.getTarget(jobType).getName();
    }

    public void votePlayer(final String name, final String targetName) {
        final Player player = players.get(name);
        final Player target = players.get(targetName);
        vote.choose(player, target);
    }

    public String getVoteResult() {
        return vote.getVoteResult();
    }

    public Long getPlayerCount() {
        return players.values().stream()
                .filter(Player::isAlive)
                .count();
    }

    public boolean isEnd() {
        final long playerCount = getPlayerCount();
        final long mafia = players.values().stream()
                .filter(player -> player.getRoleSymbol().equals(JobType.MAFIA))
                .count();
        return playerCount / 2 < mafia || mafia == 0;
    }

    public void executeVote() {
        vote.executeVote();
    }

    public void executeJobTarget() {
        jobTarget.execute();
    }
}
