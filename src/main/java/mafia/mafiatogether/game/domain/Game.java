package mafia.mafiatogether.game.domain;

import java.util.List;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.game.application.dto.event.ClearJobTargetEvent;
import mafia.mafiatogether.game.application.dto.event.ClearVoteEvent;
import mafia.mafiatogether.game.application.dto.event.DeleteGameEvent;
import mafia.mafiatogether.game.application.dto.event.JobExecuteEvent;
import mafia.mafiatogether.game.application.dto.event.StartGameEvent;
import mafia.mafiatogether.game.application.dto.event.VoteExecuteEvent;
import mafia.mafiatogether.game.domain.status.DayIntroStatus;
import mafia.mafiatogether.game.domain.status.Status;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("game")
@AllArgsConstructor
public class Game extends AbstractAggregateRoot<Game> {

    @Id
    private String code;
    private Status status;
    private LobbyInfo lobbyInfo;
    private String master;
    private PlayerCollection players;

    private transient Status statusSnapshot;

    public static Game create(final Lobby lobby, final Long now) {
        lobby.validateToStart();
        final Status status = DayIntroStatus.create(now);
        return new Game(
                lobby.getCode(),
                status,
                lobby.getLobbyInfo(),
                lobby.getMaster().getName(),
                PlayerCollection.create(lobby.getParticipants()),
                status
        );
    }

    public Game() {
        this.players = new PlayerCollection();
    }

    public void distributeRole() {
        final Queue<Job> jobs = lobbyInfo.getRandomJobQueue();
        for (Player player : players.getPlayers()) {
            if (jobs.isEmpty()) {
                break;
            }
            player.modifyJob(jobs.poll());
        }
        registerEvent(new StartGameEvent(this.code, this.players));
    }

    public StatusType getStatusType(final Long now) {
        if (status.isTimeOver(now)) {
            status = status.getNextStatus(this, now);
        }
        return status.getType();
    }

    public Player getPlayer(String name) {
        return players.findByName(name);
    }

    public boolean isMaster(Player player) {
        return this.master.equals(player.getName());
    }

    public Integer getTotalPlayers() {
        return players.getTotalPlayers();
    }

    public boolean isEnd() {
        final long aliveMafia = players.getAliveMafiaCount();
        final long aliveNotMafiaCount = players.getAliveNotMafiaCount();
        return aliveNotMafiaCount <= aliveMafia || aliveMafia == 0;
    }

    public Long getAlivePlayerCount() {
        return players.getAlivePlayerCount();
    }

    public List<Player> getWinners() {
        if (players.getWinnerJobType().equals(JobType.MAFIA)) {
            return players.getMafias();
        }
        return players.getNotMafias();
    }

    public List<Player> getLosers() {
        if (players.getWinnerJobType().equals(JobType.MAFIA)) {
            return players.getNotMafias();
        }
        return players.getMafias();
    }

    public String getWinnerJob() {
        return players.getWinnerJobType().name();
    }

    public void publishVoteExecuteEvent() {
        registerEvent(new VoteExecuteEvent(this.code));
    }

    public void executeTarget(String target) {
        players.killTarget(target);
    }

    public void publishClearVoteEvent() {
        registerEvent(new ClearVoteEvent(this.code));
    }

    public void publishJobExecuteEvent() {
        registerEvent(new JobExecuteEvent(this.code));
    }

    public void publishClearJobTargetEvent() {
        registerEvent(new ClearJobTargetEvent(this.code));
    }

    public void publishDeleteGameEvent() {
        registerEvent(new DeleteGameEvent(this.code));
    }

    public void skipStatus(final Long now) {
        status = status.getNextStatus(this, now);
    }

    public void setStatsSnapshot() {
        this.statusSnapshot = status;
    }

    public boolean isStatusChanged() {
        return !statusSnapshot.getType().equals(status.getType());
    }

    public boolean isDeleted() {
        return status.getType().equals(StatusType.DELETED);
    }

    public boolean isPlayerExist(final String name) {
        return players.contains(name);
    }
}
