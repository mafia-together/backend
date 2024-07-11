package mafia.mafiatogether.job.domain;

import java.util.Map;

public class Doctor implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.DOCTOR, player);
        return player.getName();
    }

    public static void executeSkill(Map<JobType, Player> targets) {
        final Player target = targets.getOrDefault(JobType.DOCTOR, Player.NONE);
        target.heal();
    }

    @Override
    public JobType getJobType() {
        return JobType.DOCTOR;
    }
}
