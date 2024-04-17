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
        if (!rooms.containsKey(code)) {
            throw new IllegalArgumentException("입장 코드가 올바르지 않습니다. 올바른 코드를 입력해주세요");
        }
        return rooms.get(code);
    }
}
