package mafia.mafiatogether.game.domain.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.job.domain.JobType;

public class EndStatus extends Status {

    private final List<Player> players;

    public static final Long THIRTY_SECOND = 30_000L;

    private EndStatus(final List<Player> players, final Long start, final Long end) {
        super(start, end);
        this.players = players;
    }

    public static EndStatus create(final List<Player> players, final Long now) {
        return new EndStatus(players, now, now + THIRTY_SECOND);
    }

    public JobType getWinnerJob() {
        if (getAliveMafia() == 0) {
            return JobType.CITIZEN;
        }
        return JobType.MAFIA;
    }

    private Map<Boolean, List<Player>> partitionPlayersByRole() {
        return players
                .stream()
                .collect(Collectors.partitioningBy(Player::isMafia));
    }

    public List<Player> getWinner() {
        Map<Boolean, List<Player>> players = partitionPlayersByRole();

        if (getAliveMafia() == 0) {
            return players.get(Boolean.FALSE);
        }
        return players.get(Boolean.TRUE);
    }

    public List<Player> getLoser() {
        Map<Boolean, List<Player>> playersByRole = partitionPlayersByRole();

        if (getAliveMafia() == 0) {
            return playersByRole.get(Boolean.TRUE);
        }
        return playersByRole.get(Boolean.FALSE);
    }

    private long getAliveMafia() {
        return players.stream()
                .filter(player -> player.getJobType().equals(JobType.MAFIA))
                .filter(player -> player.isAlive() == true)
                .count();
    }

    @Override
    public Status getNextStatus(final Game game, final Long now) {
        // todo : game 삭제 이벤트 날리기
        return null;
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
