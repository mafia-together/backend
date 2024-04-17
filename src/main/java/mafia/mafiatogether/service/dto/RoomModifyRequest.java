package mafia.mafiatogether.service.dto;

import mafia.mafiatogether.domain.Status;

public record RoomModifyRequest(
        Status status
) {
}
