package mafia.mafiatogether.job.domain;

import mafia.mafiatogether.global.config.exception.PlayerException;
import mafia.mafiatogether.global.config.exception.ExceptionCode;

public class Citizen implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        throw new PlayerException(ExceptionCode.INVALID_REQUEST);
    }

    @Override
    public JobType getJobType() {
        return JobType.CITIZEN;
    }
}
