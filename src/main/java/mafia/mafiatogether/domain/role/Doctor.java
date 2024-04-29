package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.Player;

public class Doctor extends Role {

    private Player target;

    public Doctor(final int limit) {
        super(limit);
    }

    @Override
    public String executeAbility(final Player player) {
        this.target = player;
        return target.getName();
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.DOCTOR;
    }
}
