package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.JobTarget;
import mafia.mafiatogether.domain.Player;

public interface Job {

    String executeAbility(final Player player, final JobTarget jobTarget);
    JobType getRoleSymbol();
}
