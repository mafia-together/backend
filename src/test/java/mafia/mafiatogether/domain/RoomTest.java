package mafia.mafiatogether.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;
import mafia.mafiatogether.domain.role.RoleSymbol;
import org.junit.jupiter.api.Test;

class RoomTest {

    @Test
    void name() {
        //given
        Room room = Room.create(new RoomInfo(4, 1, 1, 1));
        Player a = Player.create("A");
        Player b = Player.create("B");
        Player c = Player.create("C");
        Player d = Player.create("D");
        room.joinPlayer(a);
        room.joinPlayer(b);
        room.joinPlayer(c);
        room.joinPlayer(d);

        //when
        room.modifyStatus(Status.NIGHT);

        //then
        EnumSet<RoleSymbol> roleSymbols = EnumSet.noneOf(RoleSymbol.class);
        roleSymbols.add(a.getRoleSymbol());
        roleSymbols.add(b.getRoleSymbol());
        roleSymbols.add(c.getRoleSymbol());
        roleSymbols.add(d.getRoleSymbol());

        assertEquals(4, roleSymbols.size());
        assertTrue(roleSymbols.contains(RoleSymbol.CITIZEN));
        assertTrue(roleSymbols.contains(RoleSymbol.MAFIA));
        assertTrue(roleSymbols.contains(RoleSymbol.POLICE));
        assertTrue(roleSymbols.contains(RoleSymbol.DOCTOR));
    }
}
