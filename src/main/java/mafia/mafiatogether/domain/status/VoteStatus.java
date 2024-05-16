package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class VoteStatus extends Status {

    private static final Long TEN_SECOND = 10_000L;

    private VoteStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static VoteStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + TEN_SECOND;
        return new VoteStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeVote();
        return VoteResultStatus.create(clock);
    }


    @Override
    public StatusType getType() {
        return StatusType.VOTE;
    }
}
