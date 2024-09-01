package mafia.mafiatogether.game.domain.status;

import lombok.NoArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
public class DeletedStatus extends Status{

    @Override
    public Status getNextStatus(Game game, Long now) {
        throw new GameException(ExceptionCode.DELETED_STATUS);
    }

    @Override
    public StatusType getType() {
        return StatusType.DELETED;
    }
}
