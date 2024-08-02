package mafia.mafiatogether.room.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.room.application.dto.request.RoomCreateRequest;
import mafia.mafiatogether.room.application.dto.response.RoomCodeResponse;
import mafia.mafiatogether.room.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomValidateResponse;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomCodeResponse create(final RoomCreateRequest request) {
        final String code = roomRepository.create(request.toDomain());
        return new RoomCodeResponse(code);
    }

    public void join(final String code, final String name) {
        final Room room = roomRepository.findByCode(code);
        room.joinPlayer(name);
    }

    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomRepository.validateCode(code));
    }

    public RoomNightResultResponse findNightResult(final String code) {
        final Room room = roomRepository.findByCode(code);
        return new RoomNightResultResponse(room.getNightResult());
    }
}
