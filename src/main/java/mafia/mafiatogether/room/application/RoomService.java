package mafia.mafiatogether.room.application;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.global.config.exception.ExceptionCode;
import mafia.mafiatogether.global.config.exception.RoomException;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomManager;
import mafia.mafiatogether.room.domain.status.EndStatus;
import mafia.mafiatogether.room.application.dto.response.RoomCodeResponse;
import mafia.mafiatogether.room.application.dto.request.RoomCreateRequest;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.room.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.room.application.dto.response.RoomValidateResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomManager roomManager;
    private final MeterRegistry meterRegistry;

    public RoomCodeResponse create(final RoomCreateRequest request) {
        final String code = roomManager.create(request.toDomain());
        Gauge.builder("room", roomManager, RoomManager::getTotalRoomCount).tag("info", "size").register(meterRegistry);
        return new RoomCodeResponse(code);
    }

    public void join(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        room.joinPlayer(name);
    }

    public RoomStatusResponse findStatus(final String code) {
        final Room room = roomManager.findByCode(code);
        final Long now = Clock.systemDefaultZone().millis();
        return new RoomStatusResponse(room.getStatusType(now));
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomManager.findByCode(code);
        final Long now = Clock.systemDefaultZone().millis();
        room.modifyStatus(request.statusType(), now);
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Player player = room.getPlayer(name);
        return RoomInfoResponse.of(room, player, room.isMaster(player));
    }

    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomManager.validateCode(code));
    }

    public RoomResultResponse findResult(final String code) {
        final Room room = roomManager.findByCode(code);
        if (!room.isEnd()) {
            throw new RoomException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.of((EndStatus) room.getStatus());
    }

    public RoomNightResultResponse findNightResult(final String code) {
        final Room room = roomManager.findByCode(code);
        return new RoomNightResultResponse(room.getNightResult());
    }
}
