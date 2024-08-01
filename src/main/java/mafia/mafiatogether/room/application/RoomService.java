package mafia.mafiatogether.room.application;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.room.application.dto.request.RoomCreateRequest;
import mafia.mafiatogether.room.application.dto.response.RoomCodeResponse;
import mafia.mafiatogether.room.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomValidateResponse;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomManager;
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

    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomManager.validateCode(code));
    }

    public RoomNightResultResponse findNightResult(final String code) {
        final Room room = roomManager.findByCode(code);
        return new RoomNightResultResponse(room.getNightResult());
    }
}
