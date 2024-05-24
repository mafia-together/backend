package mafia.mafiatogether.domain.job;

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import mafia.mafiatogether.domain.Player;

@Getter
@Setter
public class JobTarget {

    private final Map<JobType, Player> targets;
    private Player result;

    public JobTarget() {
        this.targets = new EnumMap<>(JobType.class);
    }

    public void addTarget(final JobType jobType, final Player player) {
        targets.put(jobType, player);
    }

    public Player getTarget(final JobType jobType) {
        if (!targets.containsKey(jobType)) {
            return Player.NONE;
        }
        return targets.get(jobType);
    }

    public void execute() {
        Mafia.executeSkill(targets);
        Doctor.executeSkill(targets);
        result = targets.get(JobType.MAFIA);
        targets.clear();
    }
}
