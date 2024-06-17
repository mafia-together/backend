package mafia.mafiatogether.domain.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.job.JobType;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EndStatus extends Status {

    private List<Player> players;

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

    private Map<Boolean, List<Player>> classifyPlayersByJob() {
        return players
                .stream()
                .collect(Collectors.partitioningBy(Player::isMafia));
    }

    public List<Player> getWinner() {
        Map<Boolean, List<Player>> classifiedPlayers = classifyPlayersByJob();

        if (getAliveMafia() == 0) {
            return classifiedPlayers.get(Boolean.FALSE);
        }
        return classifiedPlayers.get(Boolean.TRUE);
    }

    public List<Player> getLoser() {
        Map<Boolean, List<Player>> classifiedPlayers = classifyPlayersByJob();

        if (getAliveMafia() == 0) {
            return classifiedPlayers.get(Boolean.TRUE);
        }
        return classifiedPlayers.get(Boolean.FALSE);
    }

    private long getAliveMafia() {
        return players.stream()
                .filter(Player::isMafia)
                .filter(Player::isAlive)
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
