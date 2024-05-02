package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.domain.Player;

public interface Job {

    String executeAbility(final Player player, final JobTarget jobTarget);
    JobType getRoleSymbol();
}
