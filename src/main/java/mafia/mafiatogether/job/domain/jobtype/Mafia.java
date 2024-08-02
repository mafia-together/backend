package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetLegacy;
import mafia.mafiatogether.job.domain.PlayerJob;

@Getter
public class Mafia implements Job {

    @Override
    public String applySkill(final Player player, final JobTargetLegacy jobTargetLegacy) {
        jobTargetLegacy.addTarget(JobType.MAFIA, player);
        return player.getName();
    }

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        return targetName;
    }

    public static void executeSkill(Map<JobType, Player> targets) {
        final Player target = targets.getOrDefault(JobType.MAFIA, Player.NONE);
        target.kill();
    }

    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }
}
