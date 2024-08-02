package mafia.mafiatogether.room.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    public static final Participant NONE = new Participant("");
    private String name;
}
