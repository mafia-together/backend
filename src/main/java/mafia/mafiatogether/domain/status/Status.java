package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.domain.Room;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Status {

    protected Long startTime;
    protected Long endTime;

    public abstract Status getNextStatus(final Room room, final Long now);

    public abstract StatusType getType();

    public boolean isTimeOver(final Long now) {
        return now >= endTime;
    }

    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    public Timestamp getEndTime() {
        return new Timestamp(endTime);
    }
}
