package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.status.StatusType;

public record RoomModifyRequest(
        StatusType statusType
) {
}
