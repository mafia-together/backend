package mafia.mafiatogether.domain;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Message {

    private final Player player;
    private final String contents;
    private final Timestamp timestamp;

    public String getName(){
        return player.getName();
    }

    public boolean isOwner(final String name){
        return player.getName().equals(name);
    }
}
