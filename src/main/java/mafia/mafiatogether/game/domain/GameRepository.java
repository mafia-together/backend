package mafia.mafiatogether.game.domain;

import java.util.Optional;

public interface GameRepository {
    Optional<Game> findById(String id);
    Game save(Game game);
}
