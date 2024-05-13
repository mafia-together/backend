package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class VoteStatus extends Status {

    private static final Long UNIT = 10000L;

    private VoteStatus(final Timestamp startTime, final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static VoteStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new VoteStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeVote();
        if (room.isEnd()) {
            return EndStatus.create(clock);
        }
        return NightStatus.create(clock);
    }


    @Override
    public StatusType getType() {
        return StatusType.VOTE;
    }
}
