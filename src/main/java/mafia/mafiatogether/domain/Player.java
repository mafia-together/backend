package mafia.mafiatogether.domain;

import static mafia.mafiatogether.domain.Role.CITIZEN;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {

    private final String name;
    private boolean alive;
    private Role role;

    public static Player create(final String name) {
        return new Player(name, true, CITIZEN);
    }

    public void modifyRole(final Role role) {
        this.role = role;
    }
}
