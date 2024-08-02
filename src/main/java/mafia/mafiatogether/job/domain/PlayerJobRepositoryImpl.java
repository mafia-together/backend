package mafia.mafiatogether.job.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerJobRepositoryImpl implements PlayerJobRepository{
    @Override
    public Optional<PlayerJob> findByCodeAndName(String code, String name) {
        return Optional.empty();
    }

    @Override
    public List<PlayerJob> findByCode(String code) {
        return List.of();
    }
}
