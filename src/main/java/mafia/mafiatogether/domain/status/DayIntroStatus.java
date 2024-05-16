package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class DayIntroStatus extends Status {

    private static final Long THREE_SECOND = 3_000L;

    private DayIntroStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static DayIntroStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + THREE_SECOND;
        return new DayIntroStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        return NoticeStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY_INTRO;
    }
}
