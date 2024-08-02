package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import java.util.Map;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Doctor implements Job {

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        return targetName;
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
