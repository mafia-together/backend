package mafia.mafiatogether.game.domain.status;

import lombok.NoArgsConstructor;
import mafia.mafiatogether.game.domain.Game;

@NoArgsConstructor
public class DayIntroStatus extends Status {

    private static final Long THREE_SECOND = 3_000L;

    private DayIntroStatus(final Long start, final Long end) {
        super(start, end);
    }

    public static DayIntroStatus create(final Long now) {
        return new DayIntroStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        return NoticeStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.DAY_INTRO;
    }
}
