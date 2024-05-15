package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class DayIntroStatus extends Status {

    private static final Long UNIT = 2999L;

    private DayIntroStatus(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    protected static DayIntroStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new DayIntroStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        return NoticeStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY_INTRO;
    }
}
