package mafia.mafiatogether.game.domain.status;

import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.room.domain.Room;

@RequiredArgsConstructor
public abstract class Status {

    protected final Long startTime;
    protected final Long endTime;

    public abstract Status getNextStatus(final Game room, final Long now);

    public abstract StatusType getType();

    public boolean isTimeOver(final Long now) {
        return now >= endTime;
    }

    public Timestamp getStartTime() {
        return new Timestamp(startTime);
    }

    public Timestamp getEndTime() {
        return new Timestamp(endTime);
    }
}
