package mafia.mafiatogether.game.domain.status;

import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.room.domain.Room;

public class NightIntroStatus extends Status {

    public static final Long THREE_SECOND = 3_000L;

    private NightIntroStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static NightIntroStatus create(final Long now) {
        return new NightIntroStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        return NightStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT_INTRO;
    }
}
