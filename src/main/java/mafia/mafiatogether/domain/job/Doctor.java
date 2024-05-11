package mafia.mafiatogether.domain.job;

import java.util.Map;
import mafia.mafiatogether.domain.Player;

public class Doctor implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.DOCTOR, player);
        return player.getName();
    }

    public static void executeSkill(Map<JobType, Player> targets) {
        final Player target = targets.getOrDefault(JobType.DOCTOR, Player.NONE);
        final Player mafiaTarget = targets.getOrDefault(JobType.MAFIA, Player.NONE);
        if (target.equals(mafiaTarget)){
            target.heal();
        }
    }

    @Override
    public JobType getRoleSymbol() {
        return JobType.DOCTOR;
    }
}
