package mafia.mafiatogether.room.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobType;
import mafia.mafiatogether.vote.domain.VoteLegacy;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private String code;
    private final Map<String, Player> players;
    private final VoteLegacy voteLegacy;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTarget jobTarget;
    private Player master;

    public static Room create(final String code, final RoomInfo roomInfo) {
        return new Room(
                code,
                new ConcurrentHashMap<>(),
                VoteLegacy.create(),
                roomInfo,
                Chat.chat(),
                new JobTarget(),
                Player.NONE
        );
    }

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                null,
                new ConcurrentHashMap<>(),
                VoteLegacy.create(),
                roomInfo,
                Chat.chat(),
                new JobTarget(),
                Player.NONE
        );
    }

    public void joinPlayer(final String name) {
        if (players.containsKey(name)) {
            throw new RoomException(ExceptionCode.INVALID_NAMES);
        }
        if (players.size() >= roomInfo.getTotal()) {
            throw new RoomException(ExceptionCode.ROOM_FULL);
        }
        final Player player = Player.create(name);
        if (master.equals(Player.NONE)) {
            master = player;
        }
        players.put(player.getName(), player);
    }

    public Player getPlayer(final String name) {
        if (name.isBlank()) {
            return Player.NONE;
        }
        if (!players.containsKey(name)) {
            throw new RoomException(ExceptionCode.INVALID_PLAYER);
        }
        return players.get(name);
    }

    public String executeSkill(final String name, final String targetName) {
        final Player player = getPlayer(name);
        final Player target = getPlayer(targetName);
        if (!target.isAlive() && !target.equals(Player.NONE)) {
            throw new PlayerException(ExceptionCode.NOT_ALIVE_PLAYER);
        }
        return player.getJob().applySkill(target, jobTarget);
    }

    public String getJobsTarget(final String name) {
        final Player player = players.get(name);
        final JobType jobType = player.getJobType();
        return jobTarget.getTargetName(jobType);
    }

    public void votePlayer(final String name, final String targetName, final Long now) {
        final Player player = getPlayer(name);
        final Player target = targetName.isBlank() ? Player.NONE : getPlayer(targetName);
        voteLegacy.choose(player, target);
        // todo : vote에서 해결
//        if (voteLegacy.isAllParticipatedVote(getPlayerCount())) {
//            this.status = status.getNextStatus(null, now);
//        }
    }

    public String getVoteResult() {
        return voteLegacy.getVoteResult();
    }

    public Long getPlayerCount() {
        return players.values().stream()
                .filter(Player::isAlive)
                .count();
    }

    public boolean isEnd() {
        long aliveMafia = getAliveMafiaCount();
        long notMafiaCount = getAlivePlayerCount();
        return notMafiaCount <= aliveMafia || aliveMafia == 0;
    }

    private long getAliveMafiaCount() {
        return players.values().stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .filter(Player::isAlive)
                .count();
    }

    private long getAlivePlayerCount() {
        return players.values().stream()
                .filter(player -> player.isAlive() && !player.isMafia())
                .count();
    }

    public void executeVote() {
        voteLegacy.executeVote();
    }

    public void executeJobTarget() {
        jobTarget.execute();
    }

    public Boolean isMaster(final Player player) {
        return this.master.equals(player);
    }

    public Integer getTotalPlayers() {
        return roomInfo.getTotal();
    }

    public boolean validateStartStatus() {
        return roomInfo.getTotal() == players.size();
    }

    public void clearVote() {
        this.voteLegacy.clear();
    }

    public void reset() {
        for (final Player player : players.values()) {
            player.reset();
        }
        voteLegacy.clear();
        chat.clear();
    }

    public String getNightResult() {
//        if (status.getType() != StatusType.NOTICE) {
//            throw new RoomException(ExceptionCode.IS_NOT_NOTICE);
//        }
        Player target = jobTarget.getResult();
        if (target.isAlive()) {
            return null;
        }
        return target.getName();
    }

    public void validateToStart() {
        if (roomInfo.getTotal() != players.size()) {
            throw new RoomException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
    }
}
