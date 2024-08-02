package mafia.mafiatogether.game.domain;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {


    @Override
    public Optional<Game> findById(String id) {
        return null;
    }

    @Override
    public Game save(Game game) {
        return null;
    }
}
