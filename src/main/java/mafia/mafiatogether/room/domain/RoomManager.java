package mafia.mafiatogether.room.domain;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import org.springframework.stereotype.Component;

@Component
public class RoomManager {

    private final Map<String, Room> rooms;

    public RoomManager() {
        this.rooms = new HashMap<>();
    }

    public String create(
            final RoomInfo roomInfo
    ) {
        String code = CodeGenerator.generate();
        while (rooms.containsKey(code)) {
            code = CodeGenerator.generate();
        }
        final Long now = Clock.systemUTC().millis();
        rooms.put(code, Room.create(roomInfo, now));
        return code;
    }

    public Room findByCode(final String code) {
        if (!rooms.containsKey(code)) {
            throw new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE);
        }
        return rooms.get(code);
    }

    public boolean validateCode(final String code) {
        return rooms.containsKey(code);
    }

    public Integer getTotalRoomCount() {
        return rooms.size();
    }
}
