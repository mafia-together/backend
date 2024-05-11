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

    public JobTarget() {
        this.targets = new EnumMap<>(JobType.class);
    }

    public void addTarget(final JobType jobType, final Player player) {
        targets.put(jobType, player);
    }

    public Player getTarget(final JobType jobType) {
        return targets.get(jobType);
    }

    public void execute(){
        Mafia.executeSkill(targets);
        Doctor.executeSkill(targets);
    }

    public void clear(){
        targets.clear();
    }
}
