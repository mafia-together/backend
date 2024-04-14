package mafia.mafiatogether.domain;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RoomManager {

    private final Map<String, Room> rooms;

    protected RoomManager() {
        this.rooms = new HashMap<>();
    }

    public String create(
            RoomInfo roomInfo
    ) {
        String code = CodeGenerator.generate();
        while (rooms.containsKey(code)) {
            code = CodeGenerator.generate();
        }
        rooms.put(code, Room.create(roomInfo));
        return code;
    }

    public Room findByCode(final String code) {
        return rooms.get(code);
    }
}
