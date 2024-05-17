package mafia.mafiatogether.domain.status;

import mafia.mafiatogether.domain.Room;

public class VoteStatus extends Status {

    private static final Long TEN_SECOND = 10_000L;

    private VoteStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static VoteStatus create(final Long now) {
        return new VoteStatus(now, now + TEN_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        room.executeVote();
        return VoteResultStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.VOTE;
    }
}
