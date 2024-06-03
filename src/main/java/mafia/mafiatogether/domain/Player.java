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

    public static final Player NONE = new Player("", true, null);
    private final String name;
    private boolean alive;
    private Job job;

    public static Player create(final String name) {
        return new Player(name, true, new Citizen());
    }

    public void modifyJob(final Job job) {
        this.job = job;
    }

    public JobType getJobType() {
        return job.getJobType();
    }

    public void kill() {
        this.alive = false;
    }

    public void heal() {
        this.alive = true;
    }

    public boolean isMafia() {
        return getJobType().equals(JobType.MAFIA);
    }

    public void reset() {
        this.alive = true;
        this.job = new Citizen();
    }
}
