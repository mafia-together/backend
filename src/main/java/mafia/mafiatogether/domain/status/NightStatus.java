package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class NightStatus extends Status {

    public static final Long UNIT = 40000L;

    public NightStatus(final Timestamp startTime, final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static NightStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new NightStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeJobTarget();
        if (room.isEnd()) {
            return EndStatus.create(clock);
        }
        return DayStatus.create(room.getPlayerCount(), clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
