package mafia.mafiatogether.domain.status;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import mafia.mafiatogether.domain.Room;

public class NightIntroStatus extends Status {

    public static final Long UNIT = 2999L;

    private NightIntroStatus(Timestamp startTime, Timestamp endTime) {
        super(startTime, endTime);
    }

    protected static NightIntroStatus create(final Clock clock) {
        final Timestamp startTime = Timestamp.valueOf(LocalDateTime.now(clock));
        final Timestamp endTime = new Timestamp(startTime.getTime() + UNIT);
        return new NightIntroStatus(startTime, endTime);
    }

    @Override
    public Status getNextStatus(Room room, Clock clock) {
        return NightStatus.create(clock);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT_INTRO;
    }
}
