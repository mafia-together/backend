package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.JobTarget;
import mafia.mafiatogether.domain.Player;

public class Citizen implements Job {

    @Override
    public String executeAbility(final Player player, final JobTarget jobTarget) {
        throw new IllegalArgumentException("시민은 능력을 사용할 수 없습니다.");
    }

    @Override
    public JobType getRoleSymbol() {
        return JobType.CITIZEN;
    }
}
