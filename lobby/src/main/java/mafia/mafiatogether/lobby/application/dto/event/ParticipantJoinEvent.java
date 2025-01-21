package mafia.mafiatogether.lobby.application.dto.event;

import mafia.mafiatogether.lobby.domain.Lobby;

public record ParticipantJoinEvent(
        Lobby lobby,
        String name
) {
}
