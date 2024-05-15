package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class DayStatus extends Status {

    private static final Long UNIT = 20000L;

    private DayStatus(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    public static DayStatus create(final Long playerCount, final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + playerCount * UNIT - 1L);
        return new DayStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        return VoteStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY;
    }
}
