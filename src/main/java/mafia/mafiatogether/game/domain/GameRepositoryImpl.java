package mafia.mafiatogether.game.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    @Override
    public Optional<Game> findById(String id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public Game save(Game game) {
        games.put(game.getCode(), game);
        return null;
    }

    @Override
    public void deleteById(String code) {
        games.remove(code);
    }
}
