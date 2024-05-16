package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class NightStatus extends Status {

    public static final Long FORTY_SECOND = 40_000L;

    private NightStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static NightStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + FORTY_SECOND;
        return new NightStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeJobTarget();
        if (room.isEnd()) {
            return EndStatus.create(clock);
        }
        return DayIntroStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
