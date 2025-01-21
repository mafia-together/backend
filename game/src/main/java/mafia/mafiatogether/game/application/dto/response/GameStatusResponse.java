package mafia.mafiatogether.game.application.dto.response;

import mafia.mafiatogether.game.domain.status.StatusType;

public record GameStatusResponse(
        StatusType statusType
) {
}
