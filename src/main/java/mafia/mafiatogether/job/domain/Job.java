package mafia.mafiatogether.job.domain;

public interface Job {
  
    String applySkill(final Player player, final JobTarget jobTarget);

    JobType getJobType();
}
