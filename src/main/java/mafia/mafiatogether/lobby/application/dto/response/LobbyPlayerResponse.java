package mafia.mafiatogether.lobby.application.dto.response;

import mafia.mafiatogether.lobby.domain.Participant;

public record LobbyPlayerResponse(
        String name
) {

    public static LobbyPlayerResponse from(Participant participant) {
        return new LobbyPlayerResponse(
                participant.getName()
        );
    }

}
