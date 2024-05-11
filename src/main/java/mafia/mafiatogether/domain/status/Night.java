package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class Night extends Status {

    public static final Long UNIT = 40000L;

    public Night(final Timestamp startTime, final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static Night create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new Night(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeJobTarget();
        if (room.isEnd()) {
            return End.create(clock);
        }
        return Day.create(room.getPlayerCount(), clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
