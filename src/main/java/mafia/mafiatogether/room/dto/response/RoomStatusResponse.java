package mafia.mafiatogether.room.dto.response;

import mafia.mafiatogether.room.domain.status.StatusType;

public record RoomStatusResponse(
        StatusType statusType

) {
}
