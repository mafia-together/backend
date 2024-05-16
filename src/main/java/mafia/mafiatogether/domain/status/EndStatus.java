package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class EndStatus extends Status {

    public static final Long ONE_MINUTE = 60_000L;

    private EndStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static EndStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + ONE_MINUTE;
        return new EndStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        return WaitStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
