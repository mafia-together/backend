package mafia.mafiatogether.room.application.dto.request;

import mafia.mafiatogether.room.domain.status.StatusType;

public record RoomModifyRequest(
        StatusType statusType
) {
}
