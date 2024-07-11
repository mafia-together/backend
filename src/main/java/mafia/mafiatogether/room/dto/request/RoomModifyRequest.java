package mafia.mafiatogether.room.dto.request;

import mafia.mafiatogether.room.domain.status.StatusType;

public record RoomModifyRequest(
        StatusType statusType
) {
}
