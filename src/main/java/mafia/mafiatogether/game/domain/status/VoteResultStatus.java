package mafia.mafiatogether.game.domain.status;

import java.util.List;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.room.domain.Room;

public class VoteResultStatus extends Status {

    public static final Long THREE_SECOND = 3_000L;

    public VoteResultStatus(final Long start, final Long end) {
        super(start, end);
    }

    protected static VoteResultStatus create(final Long now) {
        return new VoteResultStatus(now, now + THREE_SECOND);
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        // todo : vote event날리기
        if (game.isEnd()) {
            final List<Player> players = game.getPlayers().getPlayers();
            return EndStatus.create(players, now);
        }
        return NightIntroStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.VOTE_RESULT;
    }
}
