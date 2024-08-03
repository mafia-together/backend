package mafia.mafiatogether.game.domain.status;

import lombok.NoArgsConstructor;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
public class EndStatus extends Status {

    public static final Long THIRTY_SECOND = 30_000L;

    private EndStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static EndStatus create(final Long now) {
        return new EndStatus(now, now + THIRTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        // todo : game 삭제 이벤트 날리기
        return null;
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
