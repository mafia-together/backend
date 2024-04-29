package mafia.mafiatogether.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import mafia.mafiatogether.domain.role.Role;
import mafia.mafiatogether.domain.role.RoleSymbol;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {

    private final String name;
    private boolean alive;
    private Role role;

    public static Player create(final String name) {
        return new Player(name, true, null);
    }

    public void modifyRole(final Role role) {
        this.role = role;
        role.addPlayer(this);
    }

    public RoleSymbol getRoleSymbol() {
        return role.getRoleSymbol();
    }
}
