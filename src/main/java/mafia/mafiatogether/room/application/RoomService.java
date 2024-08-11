package mafia.mafiatogether.room.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.room.application.dto.request.RoomCreateRequest;
import mafia.mafiatogether.room.application.dto.response.RoomCodeResponse;
import mafia.mafiatogether.room.application.dto.response.RoomValidateResponse;
import mafia.mafiatogether.room.domain.CodeGenerator;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomInfo;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomCodeResponse create(final RoomCreateRequest request) {
        String code = CodeGenerator.generate();
        while (roomRepository.existsById(code)){
            code = CodeGenerator.generate();
        }
        final RoomInfo roomInfo = RoomInfo.of(request.total(), request.mafia(), request.doctor(), request.police());
        final Room room = Room.create(code, roomInfo);
        roomRepository.save(room);
        return new RoomCodeResponse(code);
    }

    @Transactional
    public void join(final String code, final String name) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        room.joinPlayer(name);
        roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomRepository.existsById(code));
    }
}
