package mafia.mafiatogether.domain;

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

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private final Map<String, Player> players;
    private final Vote vote;
    private Status status;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                new ConcurrentHashMap<>(),
                Vote.create(),
                Status.WAIT,
                roomInfo,
                Chat.chat(),
                new JobTarget()
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

    public String executeSkill(final String name, final Player target) {
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
        vote.choose(player, target);
    }

    public String getVoteResult() {
        return vote.getVoteResult();
    }
}
