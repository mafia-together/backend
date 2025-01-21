package mafia.mafiatogether.lobby.application.dto.response;

import java.util.List;
import mafia.mafiatogether.lobby.domain.Lobby;

public record LobbyInfoResponse(
        Integer totalPlayers,
        Boolean isMaster,
        String myName,
        List<LobbyPlayerResponse> lobbyPlayerResponses
) {

    public static LobbyInfoResponse of(Lobby lobby, String myName) {
        return new LobbyInfoResponse(
                lobby.getLobbyInfo().getTotal(),
                lobby.isMaster(myName),
                myName,
                lobby.getParticipants()
                        .getParticipants()
                        .stream()
                        .map(LobbyPlayerResponse::from)
                        .toList()
        );
    }

}
