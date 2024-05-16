package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Room;

@RequiredArgsConstructor
public abstract class Status {

    protected final Long startTime;
    protected final Long endTime;

    public abstract Status getNextStatus(final Room room, final Clock clock);

    public abstract StatusType getType();

    public boolean isTimeOver(final Clock clock) {
        final Long now = clock.millis();
        return now >= endTime;
    }

    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    public Timestamp getEndTime() {
        return new Timestamp(endTime);
    }
}
