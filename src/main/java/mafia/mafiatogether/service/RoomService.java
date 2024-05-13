package mafia.mafiatogether.service;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomInfoResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import mafia.mafiatogether.service.dto.RoomValidateResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomManager roomManager;

    public RoomCodeResponse create(final RoomCreateRequest request) {
        final String code = roomManager.create(request.toDomain());
        return new RoomCodeResponse(code);
    }

    public void join(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        room.joinPlayer(Player.create(name));
    }

    public RoomStatusResponse findStatus(final String code) {
        final Room room = roomManager.findByCode(code);
        return new RoomStatusResponse(room.getStatusType(Clock.systemDefaultZone()));
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomManager.findByCode(code);
        room.modifyStatus(request.statusType(), Clock.systemDefaultZone());
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);
        return RoomInfoResponse.of(room, player.isAlive(), room.isMaster(player));
    }

    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomManager.validateCode(code));
    }
}
