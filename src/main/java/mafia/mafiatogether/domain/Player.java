package mafia.mafiatogether.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mafia.mafiatogether.domain.role.Citizen;
import mafia.mafiatogether.domain.role.Job;
import mafia.mafiatogether.domain.role.JobType;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {

    private final String name;
    private boolean alive;
    private Job job;

    public static Player create(final String name) {
        return new Player(name, true, new Citizen());
    }

    public void modifyRole(final Job job) {
        this.job = job;
    }

    public JobType getRoleSymbol() {
        return job.getRoleSymbol();
    }
}
