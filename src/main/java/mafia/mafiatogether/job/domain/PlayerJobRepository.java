package mafia.mafiatogether.job.domain;

import java.util.List;
import java.util.Optional;

public interface PlayerJobRepository {

    Optional<PlayerJob> findByCodeAndName(String code, String name);

    List<PlayerJob> findByCode(String code);

    PlayerJob save(PlayerJob playerJob);

    void deleteAllByCode(String code);
}
