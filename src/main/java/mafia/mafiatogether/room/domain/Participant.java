package mafia.mafiatogether.room.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Participant {

    private static final String NONE_NAME = "";
    public static final Participant NONE = new Participant(NONE_NAME);
    private String name;
}
