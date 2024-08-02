package mafia.mafiatogether.room.domain;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final Map<String, Room> rooms;

    public RoomRepositoryImpl() {
        this.rooms = new HashMap<>();
    }

    @Override
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

    @Override
    public Room findByCode(final String code) {
        if (!rooms.containsKey(code)) {
            throw new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE);
        }
        return rooms.get(code);
    }

    @Override
    public boolean validateCode(final String code) {
        return rooms.containsKey(code);
    }

    @Override
    public Integer getTotalRoomCount() {
        return rooms.size();
    }
}
