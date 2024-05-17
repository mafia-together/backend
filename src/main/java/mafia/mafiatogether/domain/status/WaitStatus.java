package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.Room;

public class WaitStatus extends Status {

    private static final Long UNIT = 2999999L;

    public WaitStatus(final Timestamp startTime, final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static WaitStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new WaitStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        if (!room.validateStartStatus()) {
            throw new RoomException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
        room.distributeRole();
        return DayIntroStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.WAIT;
    }
}
