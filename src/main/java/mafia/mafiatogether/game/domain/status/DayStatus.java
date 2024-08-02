package mafia.mafiatogether.game.domain.status;

import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.room.domain.Room;

public class DayStatus extends Status {

    private static final Long TWENTY_SECOND = 20_000L;

    private DayStatus(Long start, Long end) {
        super(start, end);
    }

    public static DayStatus create(final Long playerCount, final Long now) {
        return new DayStatus(now, now + playerCount * TWENTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        return VoteStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY;
    }
}
