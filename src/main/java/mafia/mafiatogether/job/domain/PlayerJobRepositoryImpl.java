package mafia.mafiatogether.job.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerJobRepositoryImpl implements PlayerJobRepository {

    private final List<PlayerJob> playerJobs = new ArrayList<>();

    @Override
    public Optional<PlayerJob> findByCodeAndName(String code, String name) {
        return playerJobs.stream()
                .filter(playerJob -> playerJob.getName().equals(name) && playerJob.getCode().equals(code))
                .findFirst();
    }

    @Override
    public List<PlayerJob> findByCode(String code) {
        return playerJobs.stream()
                .filter(playerJob -> playerJob.getCode().equals(code))
                .toList();
    }

    @Override
    public PlayerJob save(PlayerJob playerJob) {
        playerJobs.add(playerJob);
        return playerJob;
    }

    @Override
    public void deleteAllByCode(String code) {
        List<PlayerJob> playerJobs = new ArrayList<>();
        for (PlayerJob playerJob : playerJobs) {
            if (playerJob.getCode().equals(code)) {
                playerJobs.remove(playerJob);
            }
        }
        playerJobs.forEach(playerJob -> playerJobs.remove(playerJob));
    }
}
