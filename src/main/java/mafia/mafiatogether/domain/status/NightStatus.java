package mafia.mafiatogether.domain.status;

import mafia.mafiatogether.domain.Room;

public class NightStatus extends Status {

    public static final Long FORTY_SECOND = 40_000L;

    private NightStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static NightStatus create(final Long now) {
        return new NightStatus(now, now + FORTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        room.executeJobTarget();
        if (room.isEnd()) {
            return EndStatus.create(now);
        }
        return DayIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
