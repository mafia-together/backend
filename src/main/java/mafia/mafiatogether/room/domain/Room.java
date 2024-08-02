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
import mafia.mafiatogether.job.domain.JobTargetLegacy;
import mafia.mafiatogether.job.domain.jobtype.JobType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private String code;
    private final Map<String, Player> players;
    private final RoomInfo roomInfo;
    private final Chat chat;
    private final JobTargetLegacy jobTargetLegacy;
    private Player master;

    public static Room create(final String code, final RoomInfo roomInfo) {
        return new Room(
                code,
                new ConcurrentHashMap<>(),
                roomInfo,
                Chat.chat(),
                new JobTargetLegacy(),
                Player.NONE
        );
    }

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                null,
                new ConcurrentHashMap<>(),
                roomInfo,
                Chat.chat(),
                new JobTargetLegacy(),
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
        return player.getJob().applySkill(target, jobTargetLegacy);
    }

    public String getJobsTarget(final String name) {
        final Player player = players.get(name);
        final JobType jobType = player.getJobType();
        return jobTargetLegacy.getTargetName(jobType);
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

    public void executeJobTarget() {
        jobTargetLegacy.execute();
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

    public void reset() {
        for (final Player player : players.values()) {
            player.reset();
        }
        chat.clear();
    }

    public String getNightResult() {
//        if (status.getType() != StatusType.NOTICE) {
//            throw new RoomException(ExceptionCode.IS_NOT_NOTICE);
//        }
        Player target = jobTargetLegacy.getResult();
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
