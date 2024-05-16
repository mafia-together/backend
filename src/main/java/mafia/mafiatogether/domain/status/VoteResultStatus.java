package mafia.mafiatogether.domain.status;

import mafia.mafiatogether.domain.Room;

public class VoteResultStatus extends Status {

    public static final Long THREE_SECOND = 3_000L;

    public VoteResultStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static VoteResultStatus create(final Long now) {
        return new VoteResultStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        if (room.isEnd()) {
            return EndStatus.create(now);
        }
        return NightIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.VOTE_RESULT;
    }
}
