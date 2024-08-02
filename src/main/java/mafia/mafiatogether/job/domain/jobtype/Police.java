package mafia.mafiatogether.job.domain.jobtype;

import java.util.List;
import java.util.Objects;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.JobTarget;
import mafia.mafiatogether.job.domain.JobTargetLegacy;
import mafia.mafiatogether.job.domain.PlayerJob;

public class Police implements Job {

    @Override
    public String applySkill(final Player player, final JobTargetLegacy jobTargetLegacy) {
        if (Objects.nonNull(jobTargetLegacy.getTargetName(JobType.POLICE))) {
            throw new PlayerException(ExceptionCode.POLICE_DUPLICATE_SKILL);
        }
        jobTargetLegacy.addTarget(JobType.POLICE, player);

        if (player.isMafia()) {
            return JobType.MAFIA.name();
        }
        return JobType.CITIZEN.name();
    }

    @Override
    public String applySkill(
            final List<JobTarget> jobTargets,
            final List<PlayerJob> playerJobs,
            final String targetName
    ) {
        jobTargets.stream()
                .filter(jobTarget -> jobTarget.getTarget().equals(JobType.POLICE))
                .findFirst()
                .ifPresent(jobTarget -> {throw new PlayerException(ExceptionCode.POLICE_DUPLICATE_SKILL);});
        JobType jobType = playerJobs.stream()
                .filter(playerJob -> playerJob.getName().equals(targetName))
                .findFirst()
                .orElseThrow(()-> new PlayerException(ExceptionCode.INVALID_PLAYER))
                .getJob()
                .getJobType();
        if (jobType.equals(JobType.MAFIA)){
            return JobType.MAFIA.name();
        }
        return JobType.CITIZEN.name();
    }

    @Override
    public JobType getJobType() {
        return JobType.POLICE;
    }
}
