package mafia.mafiatogether.game.domain.status;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Status {

    protected Long startTime;
    protected Long endTime;

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
