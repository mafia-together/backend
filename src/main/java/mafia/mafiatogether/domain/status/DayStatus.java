package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class DayStatus extends Status {

    private static final Long TWENTY_SECOND = 20_000L;

    private DayStatus(Long start, Long end) {
        super(start, end);
    }

    public static DayStatus create(final Long playerCount, final Clock clock) {
        final Long startTime = clock.millis();
        final Long endTime = startTime + playerCount * TWENTY_SECOND;
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
