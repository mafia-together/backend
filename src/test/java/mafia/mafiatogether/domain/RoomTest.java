package mafia.mafiatogether.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import mafia.mafiatogether.domain.role.RoleSymbol;
import org.junit.jupiter.api.Test;

class RoomTest {

    @Test
    void 직업을_배정할_수_있다() {
        //given
        Room room = Room.create(new RoomInfo(5, 3, 0, 1));
        Player a = Player.create("A");
        Player b = Player.create("B");
        Player c = Player.create("C");
        Player d = Player.create("D");
        Player e = Player.create("E");

        room.joinPlayer(a);
        room.joinPlayer(b);
        room.joinPlayer(c);
        room.joinPlayer(d);
        room.joinPlayer(e);

        //when
        room.modifyStatus(Status.NIGHT);

        //then

        List<RoleSymbol> roleSymbols = new ArrayList<>();
        roleSymbols.add(a.getRoleSymbol());
        roleSymbols.add(b.getRoleSymbol());
        roleSymbols.add(c.getRoleSymbol());
        roleSymbols.add(d.getRoleSymbol());
        roleSymbols.add(e.getRoleSymbol());

        assertEquals(5, roleSymbols.size());
        assertTrue(roleSymbols.contains(RoleSymbol.CITIZEN));
        assertTrue(roleSymbols.contains(RoleSymbol.MAFIA));
        assertTrue(roleSymbols.contains(RoleSymbol.POLICE));

        int mafiaPlayers = 0;
        for (RoleSymbol symbol : roleSymbols) {
            if (symbol == RoleSymbol.MAFIA) {
                mafiaPlayers++;
            }
        }

        assertEquals(3, mafiaPlayers);
    }
}
