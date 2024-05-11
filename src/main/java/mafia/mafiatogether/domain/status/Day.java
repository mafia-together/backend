package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class Day extends Status {

    private static final Long UNIT = 20000L;

    private Day(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    public static Day create(final Long playerCount, final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + playerCount * UNIT);
        return new Day(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        return Vote.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY;
    }
}
