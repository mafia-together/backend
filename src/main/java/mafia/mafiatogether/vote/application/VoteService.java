package mafia.mafiatogether.vote.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.vote.application.dto.event.AllPlayerVotedEvent;
import mafia.mafiatogether.vote.application.dto.response.VoteResultResponse;
import mafia.mafiatogether.vote.domain.Vote;
import mafia.mafiatogether.vote.domain.VoteRepository;
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
        final Vote vote = new Vote(code, name, targetName);
        voteRepository.save(vote);
        eventPublisher.publishEvent(new AllPlayerVotedEvent(code));
    }

    public VoteResultResponse getResult(final String code) {
        final List<Vote> votes = voteRepository.findAllByCode(code);
        return new VoteResultResponse(Vote.countVotes(votes));
    }
}
