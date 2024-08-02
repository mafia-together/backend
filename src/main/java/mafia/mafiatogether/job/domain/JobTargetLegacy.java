package mafia.mafiatogether.job.domain;

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import mafia.mafiatogether.job.domain.jobtype.Mafia;

@Getter
@Setter
public class JobTargetLegacy {

    private final Map<JobType, Player> targets;
    private Player result;

    public JobTargetLegacy() {
        this.targets = new EnumMap<>(JobType.class);
    }

    public void addTarget(final JobType jobType, final Player player) {
        targets.put(jobType, player);
    }

    public String getTargetName(final JobType jobType) {
        if (!targets.containsKey(jobType)) {
            return null;
        }
        return targets.get(jobType).getName();
    }

    public void execute() {
        Mafia.executeSkill(targets);
        Doctor.executeSkill(targets);
        result = targets.getOrDefault(JobType.MAFIA, Player.NONE);
        targets.clear();
    }
}
