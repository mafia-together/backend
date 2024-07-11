package mafia.mafiatogether.vote.service;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.repository.RoomManager;
import mafia.mafiatogether.vote.dto.response.VoteResultResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final RoomManager roomManager;

    public void votePlayer(final String code, final String name, final String targetName) {
        final Room room = roomManager.findByCode(code);
        room.votePlayer(name, targetName, Clock.systemDefaultZone().millis());
    }

    public VoteResultResponse getResult(final String code) {
        final Room room = roomManager.findByCode(code);
        return new VoteResultResponse(room.getVoteResult());
    }
}
