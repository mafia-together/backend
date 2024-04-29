package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.Player;

public class Police extends Role {

    public Police(final int limit) {
        super(limit);
    }

    @Override
    public String executeAbility(final Player player) {
        return player.getRole().getRoleSymbol().name();
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.POLICE;
    }
}
