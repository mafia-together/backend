package mafia.mafiatogether.vote.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Getter
@Repository
public class VoteRepositoryImpl implements VoteRepository {

    private final List<Vote> votes = new ArrayList<>();

    @Override
    public Vote save(Vote vote) {
        Optional<Vote> exist = votes.stream()
                .filter(v -> v.getCode().equals(vote.getCode()) && v.getName().equals(vote.getName()))
                .findFirst();
        exist.ifPresent(votes::remove);
        votes.add(vote);
        return vote;
    }

    @Override
    public List<Vote> findAllByCode(String code) {
        return votes.stream()
                .filter(vote -> vote.getCode().equals(code))
                .toList();
    }

    @Override
    public void deleteAllByCode(String code) {
        Optional<Vote> vote = votes.stream()
                .filter(v -> v.getCode().equals(code))
                .findFirst();
        vote.ifPresent(votes::remove);
    }
}
