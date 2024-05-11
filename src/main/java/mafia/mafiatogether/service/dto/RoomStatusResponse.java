package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.status.StatusType;

public record RoomStatusResponse(
        StatusType statusType

) {
}
