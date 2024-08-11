package mafia.mafiatogether.game.domain.status;

import lombok.NoArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
public class DeletedStatus extends Status{

    @Override
    public Status getNextStatus(Game room, Long now) {
        throw new RoomException(ExceptionCode.DELETED_STATUS);
    }

    @Override
    public StatusType getType() {
        return StatusType.DELETED;
    }
}
