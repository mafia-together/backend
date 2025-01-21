package mafia.mafiatogether.vote.domain;

import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, String> {
}
