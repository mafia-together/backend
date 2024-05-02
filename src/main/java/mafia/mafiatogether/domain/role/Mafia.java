package mafia.mafiatogether.domain.role;

import lombok.Getter;
import mafia.mafiatogether.domain.JobTarget;
import mafia.mafiatogether.domain.Player;

@Getter
public class Mafia implements Job {

    @Override
    public String executeAbility(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.MAFIA, player);
        return player.getName();
    }

    @Override
    public JobType getRoleSymbol() {
        return JobType.MAFIA;
    }
}
