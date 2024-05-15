package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class NoticeStatus extends Status {

    public static final Long UNIT = 2999L;

    public NoticeStatus(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    protected static NoticeStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new NoticeStatus(startTime, endTime);
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
