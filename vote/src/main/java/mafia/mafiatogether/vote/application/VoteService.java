package mafia.mafiatogether.vote.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.GameException;
import mafia.mafiatogether.vote.application.dto.event.AllPlayerVotedEvent;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void votePlayer(final String code, final String name, final String targetName) {
        final Vote vote = voteRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        vote.addVoteTarget(name, targetName);
        voteRepository.save(vote);
        eventPublisher.publishEvent(new AllPlayerVotedEvent(code));
    }

    @Transactional(readOnly = true)
    public VoteResultResponse getResult(final String code) {
        final Vote vote = voteRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new VoteResultResponse(vote.countVotes());
    }
}
