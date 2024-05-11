package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class End extends Status {

    public static final Long UNIT = 60000L;

    private End(final Timestamp startTime , final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static End create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new End(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        return Wait.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
