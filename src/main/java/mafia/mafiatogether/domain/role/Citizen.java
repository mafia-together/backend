package mafia.mafiatogether.domain.role;

import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.domain.Player;

public class Citizen implements Role {
    private final int limit;
    private final Map<String, Player> players;

    public Citizen(final int limit) {
        this.limit = limit;
        this.players = new HashMap<>();
    }

    public void addPlayer(final Player player) {
        if (players.size() >= limit) {
            throw new IllegalArgumentException("최대 시민 인원 초과");
        }
        players.put(player.getName(), player);
    }

    @Override
    public String executeAbility(final Player player) {
        throw new IllegalArgumentException("시민은 능력을 사용할 수 없습니다.");
    }

    @Override
    public boolean isOverSize() {
        return players.size() >= limit;
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.CITIZEN;
    }

    @Override
    public Player getPlayer(final String name) {
        return players.get(name);
    }
}
