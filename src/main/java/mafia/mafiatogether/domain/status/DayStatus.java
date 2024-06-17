package mafia.mafiatogether.domain.status;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.domain.Room;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayStatus extends Status {

    private static final Long TWENTY_SECOND = 20_000L;

    private DayStatus(Long start, Long end) {
        super(start, end);
    }

    public static DayStatus create(final Long playerCount, final Long now) {
        return new DayStatus(now, now + playerCount * TWENTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        return VoteStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY;
    }
}
