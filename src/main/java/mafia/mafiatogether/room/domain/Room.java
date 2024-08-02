package mafia.mafiatogether.room.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.jobtype.JobType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    private String code;
    private final Map<String, Player> players;
    private final RoomInfo roomInfo;
    private Player master;

    public static Room create(final String code, final RoomInfo roomInfo) {
        return new Room(
                code,
                new ConcurrentHashMap<>(),
                roomInfo,
                Player.NONE
        );
    }

    public static Room create(final RoomInfo roomInfo) {
        return new Room(
                null,
                new ConcurrentHashMap<>(),
                roomInfo,
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

    public void validateToStart() {
        if (roomInfo.getTotal() != players.size()) {
            throw new RoomException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
    }
}
