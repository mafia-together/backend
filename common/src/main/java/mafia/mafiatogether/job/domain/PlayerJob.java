package mafia.mafiatogether.job.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.PlayerException;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("playerJob")
@AllArgsConstructor
public class PlayerJob {

    @Id
    private String code;
    private Map<String, Job> playerJobs;

    public PlayerJob() {
        this.playerJobs = new HashMap<>();
    }

    public Job findJobByName(String name) {
        if (!playerJobs.containsKey(name)) {
            throw new PlayerException(ExceptionCode.INVALID_PLAYER);
        }
        return playerJobs.get(name);
    }

    public boolean isMafia(String name) {
        return findJobByName(name).getJobType().equals(JobType.MAFIA);
    }

    public void add(String name, Job job) {
        this.playerJobs.put(name, job);
    }
}
