package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.CreateRoomRequest;
import mafia.mafiatogether.service.dto.CreateRoomResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomManager roomManager;

    public CreateRoomResponse create(final CreateRoomRequest request) {
        String code = roomManager.create(request.toDomain());
        return new CreateRoomResponse(code);
    }
}
