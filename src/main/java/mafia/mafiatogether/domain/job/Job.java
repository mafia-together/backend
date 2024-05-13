package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.domain.Player;

public interface Job {
  
    String applySkill(final Player player, final JobTarget jobTarget);

    JobType getJobSymbol();
}
