package mafia.mafiatogether.domain.role;

import lombok.Getter;
import mafia.mafiatogether.domain.Player;

@Getter
public class Mafia extends Role {

    private Player target;

    public Mafia(final int limit) {
        super(limit);
    }

    @Override
    public String executeAbility(final Player player) {
        this.target = player;
        return target.getName();
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.MAFIA;
    }
}
