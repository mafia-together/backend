package mafia.mafiatogether.domain.job;

import java.util.Map;
import lombok.Getter;
import mafia.mafiatogether.domain.Player;

@Getter
public class Mafia implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.MAFIA, player);
        return player.getName();
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
