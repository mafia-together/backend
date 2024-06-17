package mafia.mafiatogether.domain.status;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.domain.Room;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeStatus extends Status {

    public static final Long THREE_SECOND = 3000L;

    private NoticeStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static NoticeStatus create(final Long now) {
        return new NoticeStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        return DayStatus.create(room.getPlayerCount(), now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NOTICE;
    }
}
