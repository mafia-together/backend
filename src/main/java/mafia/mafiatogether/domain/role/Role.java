package mafia.mafiatogether.domain.role;

import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.domain.Player;

public abstract class Role {

    protected final int limit;
    protected final Map<String, Player> players;

    public Role(final int limit) {
        this.limit = limit;
        this.players = new HashMap<>();
    }

    public void addPlayer(final Player player) {
        if (players.size() >= limit) {
            throw new IllegalArgumentException("최대 인원 초과");
        }
        players.put(player.getName(), player);
    }

    public Player getPlayer(final String name) {
        return players.get(name);
    }

    public boolean isPlayerOverLimit() {
        return players.size() >= limit;
    }

    public abstract String executeAbility(final Player player);

    public abstract RoleSymbol getRoleSymbol();
}
