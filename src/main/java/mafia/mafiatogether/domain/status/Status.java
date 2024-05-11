package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Room;

@RequiredArgsConstructor
public abstract class Status {

    protected final Timestamp startTime;
    protected final Timestamp endTime;

    public abstract Status getNextStatus(final Room room, final Clock clock);

    public abstract StatusType getType();

    public boolean isTimeOver(final Clock clock) {
        final Timestamp now = Timestamp.valueOf(LocalDateTime.now(clock));
        return now.after(endTime);
    }
}
