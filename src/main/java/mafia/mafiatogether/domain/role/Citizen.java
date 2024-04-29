package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.Player;

public class Citizen extends Role {

    public Citizen(final int limit) {
        super(limit);
    }

    @Override
    public String executeAbility(final Player player) {
        throw new IllegalArgumentException("시민은 능력을 사용할 수 없습니다.");
    }

    @Override
    public RoleSymbol getRoleSymbol() {
        return RoleSymbol.CITIZEN;
    }
}
