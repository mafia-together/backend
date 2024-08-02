package mafia.mafiatogether.vote.domain;

import java.util.List;

public interface VoteRepository {
    Vote save(Vote vote);

    List<Vote> findAllByCode(String code);

    void deleteAllByCode(String code);
}
