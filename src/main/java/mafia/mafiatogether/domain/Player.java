package mafia.mafiatogether.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.domain.job.Citizen;
import mafia.mafiatogether.domain.job.Job;
import mafia.mafiatogether.domain.job.JobType;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {

    public static final Player NONE = new Player("", null, false, null);
    private final String name;
    private Player vote;
    private boolean alive;
    private Job job;

    public static Player create(final String name) {
        return new Player(name, NONE, true, new Citizen());
    }

    public void modifyRole(final Job job) {
        this.job = job;
    }

    public JobType getRoleSymbol() {
        return job.getRoleSymbol();
    }

    public void setVote(final Player player) {
        this.vote = player;
    }

    public void clear() {
        this.vote = NONE;
    }
}
