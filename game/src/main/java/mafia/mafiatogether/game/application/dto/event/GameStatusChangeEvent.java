package mafia.mafiatogether.game.application.dto.event;

import mafia.mafiatogether.game.domain.status.StatusType;

public record GameStatusChangeEvent(
        String code,
        StatusType statusType
) {
}
