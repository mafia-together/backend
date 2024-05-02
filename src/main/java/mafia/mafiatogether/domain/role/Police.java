package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.JobTarget;
import mafia.mafiatogether.domain.Player;

public class Police implements Job {

    @Override
    public String executeAbility(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.POLICE, player);
        return player.getJob().getRoleSymbol().name();
    }

    @Override
    public JobType getRoleSymbol() {
        return JobType.POLICE;
    }
}
