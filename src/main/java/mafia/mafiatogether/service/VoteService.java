package mafia.mafiatogether.service;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.VoteResultResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final RoomManager roomManager;

    public void votePlayer(final String code, final String name, final String targetName) {
        final Room room = roomManager.findByCode(code);
        room.votePlayer(name, targetName);
    }

    public VoteResultResponse getResult(final String code) {
        final Room room = roomManager.findByCode(code);
        return new VoteResultResponse(room.getVoteResult());
    }
}
