package mafia.mafiatogether.domain.role;

import mafia.mafiatogether.domain.Player;

public interface Role {

    boolean isOverSize();
    RoleSymbol getRoleSymbol();
    Player getPlayer(String name);
    String executeAbility(Player player);
}
