package mafia.mafiatogether.domain;

import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
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
        if (!rooms.containsKey(code)) {
            throw new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE);
        }
        return rooms.get(code);
    }

    public boolean validateCode(final String code) {
        return !rooms.containsKey(code);
    }
}
