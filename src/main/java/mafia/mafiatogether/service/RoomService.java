package mafia.mafiatogether.service;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.CodeGenerator;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomInfo;
import mafia.mafiatogether.domain.status.EndStatus;
import mafia.mafiatogether.domain.status.StatusType;
import mafia.mafiatogether.repository.RoomRepository;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomInfoResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomNightResultResponse;
import mafia.mafiatogether.service.dto.RoomResultResponse;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import mafia.mafiatogether.service.dto.RoomValidateResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomCodeResponse create(final RoomCreateRequest request) {
        String code = CodeGenerator.generate();
        while (roomRepository.existsById(code)){
            code = CodeGenerator.generate();
        }
        final RoomInfo roomInfo = RoomInfo.of(request.total(), request.mafia(), request.doctor(), request.police());
        final Room room = Room.create(code, roomInfo, Clock.systemDefaultZone().millis());
        roomRepository.save(room);
        return new RoomCodeResponse(code);
    }

    public void join(final String code, final String name) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        room.joinPlayer(name);
        roomRepository.save(room);
    }

    public RoomStatusResponse findStatus(final String code) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Long now = Clock.systemDefaultZone().millis();
        final StatusType statusType = room.getStatusType(now);
        roomRepository.save(room);
        return new RoomStatusResponse(statusType);
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Long now = Clock.systemDefaultZone().millis();
        room.modifyStatus(request.statusType(), now);
        roomRepository.save(room);
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Player player = room.getPlayer(name);
        return RoomInfoResponse.of(room, player, room.isMaster(player));
    }

    public RoomValidateResponse validateCode(final String code) {
        return new RoomValidateResponse(roomRepository.existsById(code));
    }

    public RoomResultResponse findResult(final String code) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!room.isEnd()) {
            throw new RoomException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.of((EndStatus) room.getStatus());
    }

    public RoomNightResultResponse findNightResult(final String code) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new RoomNightResultResponse(room.getNightResult());
    }
}
