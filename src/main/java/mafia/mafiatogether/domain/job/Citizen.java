package mafia.mafiatogether.domain.job;

import mafia.mafiatogether.config.exception.CitizenException;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.domain.Player;

public class Citizen implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        throw new CitizenException(ExceptionCode.INVALID_REQUEST);
    }

    @Override
    public JobType getJobSymbol() {
        return JobType.CITIZEN;
    }
}
