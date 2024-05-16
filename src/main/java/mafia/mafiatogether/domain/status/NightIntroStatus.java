package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class NightIntroStatus extends Status {

    public static final Long THREE_SECOND = 3_000L;

    private NightIntroStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static NightIntroStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + THREE_SECOND;
        return new NightIntroStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        return NightStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT_INTRO;
    }
}
