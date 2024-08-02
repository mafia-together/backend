package mafia.mafiatogether.job.domain;

import mafia.mafiatogether.game.domain.Player;

public interface Job {
  
    String applySkill(final Player player, final JobTarget jobTarget);

    JobType getJobType();
}
