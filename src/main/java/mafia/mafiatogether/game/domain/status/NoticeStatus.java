package mafia.mafiatogether.game.domain.status;

import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.room.domain.Room;

public class NoticeStatus extends Status {

    public static final Long THREE_SECOND = 3000L;

    private NoticeStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static NoticeStatus create(final Long now) {
        return new NoticeStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        return DayStatus.create(game.getAlivePlayerCount(), now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NOTICE;
    }
}
