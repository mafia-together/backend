package mafia.mafiatogether.game.application.dto.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.job.application.dto.response.PlayerResponse;

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
            final Game game,
            final Player player,
            final Boolean isMaster
    ) {
        return new RoomInfoResponse(
                game.getStatus().getStartTime(),
                game.getStatus().getEndTime(),
                player.isAlive(),
                game.getTotalPlayers(),
                isMaster,
                player.getName(),
                convertFrom(player, game.getPlayers())
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
