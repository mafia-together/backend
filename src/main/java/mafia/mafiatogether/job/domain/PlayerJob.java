package mafia.mafiatogether.job.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.job.domain.jobtype.Job;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJob {

    private String code;
    private String name;
    private Job job;

    @JsonIgnore
    public String getId() {
        return code + ":" + name;
    }
}
