package mafia.mafiatogether.domain.role;

import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.domain.Player;

public class Doctor implements Role{

    private final int limit;
    private final Map<String, Player> players;
    private Player target;

    public Doctor(final int limit) {
        this.limit = limit;
        this.players = new HashMap<>();
    }

    @Override
    public void addPlayer(final Player player) {
        if (players.size() >= limit) {
            throw new IllegalArgumentException("최대 의사 인원 초과");
        }
        players.put(player.getName(), player);
    }

    @Override
    public String executeAbility(final Player player) {
        this.target = player;
        return target.getName();
    }

    @Override
    public boolean isOverSize() {
        return players.size() >= limit;
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.DOCTOR;
    }

    @Override
    public Player getPlayer(final String name) {
        return players.get(name);
    }
}
