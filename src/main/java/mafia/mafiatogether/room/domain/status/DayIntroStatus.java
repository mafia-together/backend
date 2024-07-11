package mafia.mafiatogether.room.domain.status;

import mafia.mafiatogether.room.domain.Room;

public class DayIntroStatus extends Status {

    private static final Long THREE_SECOND = 3_000L;

    private DayIntroStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static DayIntroStatus create(final Long now) {
        return new DayIntroStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        return NoticeStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY_INTRO;
    }
}
