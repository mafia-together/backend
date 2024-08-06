package mafia.mafiatogether.vote.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteTarget {

    private String code;
    private String name;
    private String target;
}
