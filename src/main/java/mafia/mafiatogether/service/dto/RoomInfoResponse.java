package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;

public record RoomInfoResponse(
        Timestamp startTime,
        Timestamp endTime,
        Boolean isAlive,
        Integer totalPlayers,
        Boolean isMaster,
        String myName,
        List<PlayerResponse> players
) {

    public static RoomInfoResponse of(
            final Room room,
            final Player player,
            final Boolean isMaster
    ) {
        return new RoomInfoResponse(
                room.getStatus().getStartTime(),
                room.getStatus().getEndTime(),
                player.isAlive(),
                room.getTotalPlayers(),
                isMaster,
                player.getName(),
                convertFrom(player, room.getPlayers())
        );
    }

    private static List<PlayerResponse> convertFrom(final Player player, final Map<String, Player> players) {
        if (!player.isAlive()) {
            return players.values().stream()
                    .map(PlayerResponse::forDead)
                    .toList();
        }
        if (player.isMafia()) {
            return players.values().stream()
                    .map(PlayerResponse::forMafia)
                    .toList();
        }
        return players.values().stream()
                .map(PlayerResponse::forAlive)
                .toList();
    }
}
