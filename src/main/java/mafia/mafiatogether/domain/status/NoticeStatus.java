package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class NoticeStatus extends Status {

    public static final Long THREE_SECOND = 3000L;

    private NoticeStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static NoticeStatus create(final Clock clock) {
        return new NoticeStatus(clock.millis(), clock.millis() + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        return DayStatus.create(room.getPlayerCount(), clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NOTICE;
    }
}
