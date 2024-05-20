package mafia.mafiatogether.domain.status;

import java.util.List;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;

public class NightStatus extends Status {

    public static final Long FORTY_SECOND = 40_000L;

    private NightStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static NightStatus create(final Long now) {
        return new NightStatus(now, now + FORTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Room room, final Long now) {
        room.executeJobTarget();
        final List<Player> players = room.getPlayers()
                .values()
                .stream()
                .toList();
        if (room.isEnd()) {
            return EndStatus.create(players, now);
        }
        return DayIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
