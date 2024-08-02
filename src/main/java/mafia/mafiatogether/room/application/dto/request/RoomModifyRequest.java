package mafia.mafiatogether.room.application.dto.request;

import mafia.mafiatogether.game.domain.status.StatusType;

public record RoomModifyRequest(
        StatusType statusType
) {
}
