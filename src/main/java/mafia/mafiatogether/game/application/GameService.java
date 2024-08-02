package mafia.mafiatogether.game.application;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.room.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomRepository;
import mafia.mafiatogether.room.domain.status.EndStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;

    public RoomStatusResponse findStatus(final String code) {
        final Room room = roomRepository.findByCode(code);
        final Long now = Clock.systemDefaultZone().millis();
        return new RoomStatusResponse(room.getStatusType(now));
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomRepository.findByCode(code);
        final Long now = Clock.systemDefaultZone().millis();
        room.modifyStatus(request.statusType(), now);
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Room room = roomRepository.findByCode(code);
        final Player player = room.getPlayer(name);
        return RoomInfoResponse.of(room, player, room.isMaster(player));
    }

    public RoomResultResponse findResult(final String code) {
        final Room room = roomRepository.findByCode(code);
        if (!room.isEnd()) {
            throw new RoomException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.of((EndStatus) room.getStatus());
    }
}
