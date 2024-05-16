package mafia.mafiatogether.domain.status;

import mafia.mafiatogether.domain.Room;

public class EndStatus extends Status {

    public static final Long ONE_MINUTE = 60_000L;

    private EndStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static EndStatus create(final Long now) {
        return new EndStatus(now, now + ONE_MINUTE);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        return WaitStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
