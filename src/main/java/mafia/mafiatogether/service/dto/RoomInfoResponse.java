package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    private static List<PlayerResponse> convertFrom(final Player owner, final Map<String, Player> players) {
        PlayerResponse myJob = PlayerResponse.forMyJob(players.get(owner.getName()));
        List<PlayerResponse> responses = players.values().stream()
                .filter(it -> !it.getName().equals(owner.getName()))
                .map(it -> playerToResponse(owner, it))
                .collect(Collectors.toList());
        responses.add(myJob);
        return responses;
    }

    private static PlayerResponse playerToResponse(final Player owner, final Player player) {
        if (!owner.isAlive()) {
            return PlayerResponse.forDead(player);
        }
        if (owner.isMafia()) {
            return PlayerResponse.forMafia(player);
        }
        return PlayerResponse.forAlive(player);

    }
}
