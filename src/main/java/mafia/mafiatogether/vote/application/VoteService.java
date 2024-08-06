package mafia.mafiatogether.vote.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.vote.application.dto.event.AllPlayerVotedEvent;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepository;
import mafia.mafiatogether.vote.domain.VoteTarget;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void votePlayer(final String code, final String name, final String targetName) {
        final Vote vote = voteRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final VoteTarget voteTarget = new VoteTarget(code, name, targetName);
        vote.addVoteTarget(voteTarget);
        voteRepository.save(vote);
        eventPublisher.publishEvent(new AllPlayerVotedEvent(code));
    }

    public VoteResultResponse getResult(final String code) {
        final Vote vote = voteRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new VoteResultResponse(vote.countVotes());
    }
}
