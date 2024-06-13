package mafia.mafiatogether.domain.job;

import java.util.Objects;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.domain.Player;

public class Police implements Job {

    @Override
    public String applySkill(final Player player, final JobTarget jobTarget) {
        if (Objects.nonNull(jobTarget.getTargetName(JobType.POLICE))) {
            throw new PlayerException(ExceptionCode.POLICE_DUPLICATE_SKILL);
        }
        jobTarget.addTarget(JobType.POLICE, player);

        if (player.isMafia()) {
            return JobType.MAFIA.name();
        }
        return JobType.CITIZEN.name();
    }

    @Override
    public JobType getJobType() {
        return JobType.POLICE;
    }
}
