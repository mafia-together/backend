package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.domain.Player;

public class Police implements Job {

    @Override
    public String executeAbility(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.POLICE, player);
        return player.getJob().getJobSymbol().name();
    }

    @Override
    public JobType getJobSymbol() {
        return JobType.POLICE;
    }
}
