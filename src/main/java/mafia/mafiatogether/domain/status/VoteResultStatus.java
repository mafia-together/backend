package mafia.mafiatogether.domain.status;

import java.time.Clock;
import mafia.mafiatogether.domain.Room;

public class VoteResultStatus extends Status {

    public static final Long THREE_SECOND = 3_000L;

    public VoteResultStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static VoteResultStatus create(final Clock clock) {
        final Long startTime = clock.millis();
        final long endTime = clock.millis() + THREE_SECOND;
        return new VoteResultStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        if (room.isEnd()) {
            return EndStatus.create(clock);
        }
        return NightIntroStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.VOTE_RESULT;
    }
}
