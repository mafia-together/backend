package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.domain.Player;

public class Police implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.POLICE, player);
        return player.getJob().getJobType().name();
    }

    @Override
    public JobType getJobType() {
        return JobType.POLICE;
    }
}
