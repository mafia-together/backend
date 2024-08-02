package mafia.mafiatogether.game.domain;

import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.game.domain.status.DayIntroStatus;
import mafia.mafiatogether.game.domain.status.Status;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.job.domain.Job;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;

@Getter
@AllArgsConstructor
public class Game {

    // id
    private String code;
    private Status status;
    private RoomInfo roomInfo;
    private String master;
    private PlayerCollection players;


    public static Game create(final Room room, final Long now) {
        return new Game(
                room.getCode(),
                DayIntroStatus.create(now),
                room.getRoomInfo(),
                room.getMaster().getName(),
                new PlayerCollection(room.getPlayers().values().stream().toList())
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
    }

    // status 로직 넣기
    public StatusType getStatusType(final Long now) {
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
}
