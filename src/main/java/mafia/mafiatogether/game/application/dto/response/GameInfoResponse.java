package mafia.mafiatogether.game.application.dto.response;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.PlayerCollection;
import mafia.mafiatogether.lobby.domain.ParticipantCollection;
import mafia.mafiatogether.lobby.domain.Lobby;

public record GameInfoResponse(
        Timestamp startTime,
        Timestamp endTime,
        Boolean isAlive,
        Integer totalPlayers,
        Boolean isMaster,
        String myName,
        List<PlayerResponse> players
) {
    public static GameInfoResponse ofGame(final Game game, final String name) {
        final Player player = game.getPlayer(name);
        return new GameInfoResponse(
                game.getStatus().getStartTime(),
                game.getStatus().getEndTime(),
                player.isAlive(),
                game.getTotalPlayers(),
                game.isMaster(player),
                player.getName(),
                convertFrom(player, game.getPlayers())
        );
    }

    public static GameInfoResponse ofLobby(Lobby lobby, String name) {
        return new GameInfoResponse(
                new Timestamp(Clock.systemDefaultZone().millis()),
                new Timestamp(Clock.systemDefaultZone().millis()),
                true,
                lobby.getLobbyInfo().getTotal(),
                lobby.getMaster().getName().equals(name),
                name,
                convertFrom(lobby.getParticipants())
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
