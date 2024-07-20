package mafia.mafiatogether.room.domain.status;

import mafia.mafiatogether.global.config.exception.ExceptionCode;
import mafia.mafiatogether.global.config.exception.RoomException;
import mafia.mafiatogether.room.domain.Room;

public class WaitStatus extends Status {

    private static final Long THIRTY_MINUTE = 300_000L;

    private WaitStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static WaitStatus create(final Long now) {
        return new WaitStatus(now, now + THIRTY_MINUTE);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        if (!room.validateStartStatus()) {
            throw new RoomException(ExceptionCode.NOT_ENOUGH_PLAYER);
        }
        room.distributeRole();
        return DayIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.WAIT;
    }
}
