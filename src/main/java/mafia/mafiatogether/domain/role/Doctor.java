package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.JobTarget;
import mafia.mafiatogether.domain.Player;

public class Doctor implements Job {

    @Override
    public String executeAbility(final Player player, final JobTarget jobTarget) {
        jobTarget.addTarget(JobType.DOCTOR, player);
        return player.getName();
    }

    @Override
    public JobType getRoleSymbol() {
        return JobType.DOCTOR;
    }
}
