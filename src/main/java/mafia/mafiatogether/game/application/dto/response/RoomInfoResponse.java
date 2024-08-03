package mafia.mafiatogether.game.application.dto.response;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.room.domain.ParticipantCollection;
import mafia.mafiatogether.room.domain.Room;

public record RoomInfoResponse(
        Timestamp startTime,
        Timestamp endTime,
        Boolean isAlive,
        Integer totalPlayers,
        Boolean isMaster,
        String myName,
        List<PlayerResponse> players
) {
    public static RoomInfoResponse ofGame(final Game game, final String name) {
        final Player player = game.getPlayer(name);
        return new RoomInfoResponse(
                game.getStatus().getStartTime(),
                game.getStatus().getEndTime(),
                player.isAlive(),
                game.getTotalPlayers(),
                game.isMaster(player),
                player.getName(),
                convertFrom(player, game.getPlayers())
        );
    }

    public static RoomInfoResponse ofRoom(Room room, String name) {
        return new RoomInfoResponse(
                new Timestamp(Clock.systemDefaultZone().millis()),
                new Timestamp(Clock.systemDefaultZone().millis()),
                true,
                room.getRoomInfo().getTotal(),
                room.getMaster().getName().equals(name),
                name,
                convertFrom(room.getParticipants())
        );
    }

    private static List<PlayerResponse> convertFrom(final Player owner, final PlayerCollection players) {
        PlayerResponse myJob = PlayerResponse.forMyJob(players.findByName(owner.getName()));
        List<PlayerResponse> responses = players.getPlayers().stream()
                .filter(response -> !response.getName().equals(owner.getName()))
                .map(response -> playerToResponse(owner, response))
                .collect(Collectors.toList());
        responses.add(myJob);
        return responses;
    }

    private static List<PlayerResponse> convertFrom(final ParticipantCollection participantCollection) {
        return participantCollection.getParticipants().stream()
                .map(participant -> PlayerResponse.forLobby(participant.getName()))
                .toList();
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
