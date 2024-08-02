package mafia.mafiatogether.job.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobTarget {

    private String code;
    private String name;
    private String target;
}
