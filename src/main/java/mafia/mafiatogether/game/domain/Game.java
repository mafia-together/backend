package mafia.mafiatogether.game.domain;

import java.util.List;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.game.application.dto.event.ClearJobTargetEvent;
import mafia.mafiatogether.game.application.dto.event.CreatePlayerJobEvent;
import mafia.mafiatogether.game.application.dto.event.DeleteGameEvent;
import mafia.mafiatogether.game.application.dto.event.JobExecuteEvent;
import mafia.mafiatogether.game.application.dto.event.VoteExecuteEvent;
import mafia.mafiatogether.game.domain.status.DayIntroStatus;
import mafia.mafiatogether.game.domain.status.Status;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
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
    private RoomInfo roomInfo;
    private String master;
    private PlayerCollection players;


    public static Game create(final Room room, final Long now) {
        room.validateToStart();
        return new Game(
                room.getCode(),
                DayIntroStatus.create(now),
                room.getRoomInfo(),
                room.getMaster().getName(),
                PlayerCollection.creat(room.getParticipants())
        );
    }

    public void distributeRole() {
        final Queue<Job> jobs = roomInfo.getRandomJobQueue();
        for (Player player : players.getPlayers()) {
            if (jobs.isEmpty()) {
                break;
            }
            player.modifyJob(jobs.poll());
        }
        registerEvent(new CreatePlayerJobEvent(this.code, this.players));
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
        if (players.getWinnerJobType().equals(JobType.MAFIA)){
            return players.getNotMafias();
        }
        return players.getMafias();
    }

    public String getWinnerJob() {
        return players.getWinnerJobType().name();
    }

    public void publishVoteExecuteEvent(){
        registerEvent(new VoteExecuteEvent(this.code));
    }

    public void executeTarget(String target) {
        players.executeTarget(target);
    }

    public void publicJobExecuteEvent() {
        registerEvent(new JobExecuteEvent(this.code));
    }

    public void publishClearJobTargetEvent() {
        registerEvent(new ClearJobTargetEvent(this.code));
    }

    public void publishDeleteGameEvent(){
        registerEvent(new DeleteGameEvent(this.code));
    }
}
