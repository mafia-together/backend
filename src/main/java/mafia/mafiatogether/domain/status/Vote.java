package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class Vote extends Status {

    private static final Long UNIT = 10000L;

    private Vote(final Timestamp startTime, final Timestamp endTime) {
        super(startTime, endTime);
    }

    public static Vote create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new Vote(startTime, endTime);
    }

    @Override
    public Status getNextStatus(final Room room, final Clock clock) {
        room.executeVote();
        if (room.isEnd()) {
            return End.create(clock);
        }
        return Night.create(clock);
    }


    @Override
    public StatusType getType() {
        return StatusType.VOTE;
    }
}
