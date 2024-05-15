package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class VoteResultStatus extends Status {

    public static final Long UNIT = 2999L;

    public VoteResultStatus(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    protected static VoteResultStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
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
