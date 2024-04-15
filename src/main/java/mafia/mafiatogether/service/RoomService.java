package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomCreateResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomManager roomManager;

    public RoomCreateResponse create(final RoomCreateRequest request) {
        String code = roomManager.create(request.toDomain());
        return new RoomCreateResponse(code);
    }

    public void join(final String code, final String name) {
        Room room = roomManager.findByCode(code);
        room.joinPlayer(new Player(name));
    }

    public RoomStatusResponse findStatus(final String code) {
        Room room = roomManager.findByCode(code);
        return new RoomStatusResponse(room.getStatus());
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        Room room = roomManager.findByCode(code);
        room.modifyStatus(request.status());
    }
}
