package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.Room;

public class WaitStatus extends Status {

    private static final Long THIRTY_MINUTE = 300_000L;

    private WaitStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static WaitStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + THIRTY_MINUTE;
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
