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
        Integer totalPlayer,
        Boolean isMaster,
        List<PlayerResponse> players
) {

    public static RoomInfoResponse of(
            final Room room,
            final Boolean isAlive,
            final Boolean isMaster
    ) {
        return new RoomInfoResponse(
                room.getStatus().getStartTime(),
                room.getStatus().getEndTime(),
                isAlive,
                room.getPlayers().size(),
                isMaster,
                convertFrom(isAlive, room.getPlayers())
        );
    }

    private static List<PlayerResponse> convertFrom(final boolean isAlive, final Map<String, Player> players) {
        if (isAlive) {
            return players.values().stream()
                    .map(PlayerResponse::forAlive)
                    .toList();
        }
        return players.values().stream()
                .map(PlayerResponse::forDead)
                .toList();
    }
}
