package mafia.mafiatogether.game.application.dto.request;

import mafia.mafiatogether.game.domain.status.StatusType;

public record RoomModifyRequest(
        StatusType statusType
) {
}
