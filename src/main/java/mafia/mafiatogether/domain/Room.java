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
import mafia.mafiatogether.domain.status.WaitStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final Map<String, Player> players;
    private final Vote vote;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;
    private Player master;

    public static Room create(final RoomInfo roomInfo, final Clock clock) {
        return new Room(
                new ConcurrentHashMap<>(),
                Vote.create(),
                WaitStatus.create(clock),
                roomInfo,
                Chat.chat(),
                new JobTarget(),
                Player.NONE
        );
    }

    public StatusType getStatusType(final Clock clock) {
        if (status.isTimeOver(clock)) {
            status = status.getNextStatus(this, clock);
        }
        return status.getType();
    }

    public void modifyStatus(final StatusType statusType, final Clock clock) {
        this.status = status.getNextStatus(this, clock);
    }

    public void joinPlayer(final Player player) {
        if (master.equals(Player.NONE)) {
            master = player;
        }
        players.put(player.getName(), player);
    }

    public void distributeRole() {
        final Queue<Job> jobs = roomInfo.getRandomJobQueue();
        for (Player player : players.values()) {
            if (jobs.isEmpty()) {
                break;
            }
            player.modifyJob(jobs.poll());
        }
    }

    public Player getPlayer(final String name) {
        if (!players.containsKey(name)) {
            throw new RoomException(ExceptionCode.INVALID_PLAYER);
        }
        return players.get(name);
    }

    public String executeSkill(final String name, final Player target) {
        final Player player = players.get(name);
        return player.getJob().applySkill(target, jobTarget);
    }

    public String getJobsTarget(final String name) {
        final Player player = players.get(name);
        final JobType jobType = player.getJobSymbol();
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
                .filter(player -> player.getJobSymbol().equals(JobType.MAFIA))
                .count();
        return playerCount / 2 < mafia || mafia == 0;
    }

    public void executeVote() {
        vote.executeVote();
    }

    public void executeJobTarget() {
        jobTarget.execute();
    }

    public Boolean isMaster(final Player player) {
        return this.master.equals(player);
    }

    public Integer getTotalPlayers(){
        return roomInfo.getTotal();
    }
}
