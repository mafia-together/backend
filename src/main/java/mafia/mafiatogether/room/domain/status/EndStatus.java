package mafia.mafiatogether.room.domain.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mafia.mafiatogether.job.domain.Player;
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
    public Status getNextStatus(final Room room, final Long now) {
        room.reset();
        return WaitStatus.create(now);
    }

    @Override
    public StatusType getType() {
        return StatusType.END;
    }
}
