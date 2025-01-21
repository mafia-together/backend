package mafia.mafiatogether.game.domain.status;

import lombok.NoArgsConstructor;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
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
        game.publishJobExecuteEvent();
        return DayIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.NIGHT;
    }
}
