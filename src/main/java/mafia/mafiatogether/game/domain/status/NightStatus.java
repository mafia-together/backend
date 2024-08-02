package mafia.mafiatogether.game.domain.status;

import java.util.List;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.room.domain.Room;

public class NightStatus extends Status {

    public static final Long FORTY_SECOND = 40_000L;

    private NightStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static NightStatus create(final Long now) {
        return new NightStatus(now, now + FORTY_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        if (game.isEnd()) {
            return EndStatus.create(game.getPlayers().getPlayers(), now);
        }
        return DayIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
