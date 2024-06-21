package mafia.mafiatogether.service;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.repository.RoomRepository;
import mafia.mafiatogether.service.dto.VoteResultResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final RoomRepository roomRepository;

    public void votePlayer(final String code, final String name, final String targetName) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        room.votePlayer(name, targetName, Clock.systemDefaultZone().millis());
        roomRepository.save(room);
    }

    public VoteResultResponse getResult(final String code) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new VoteResultResponse(room.getVoteResult());
    }
}
