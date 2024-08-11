package mafia.mafiatogether.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String name;
    private String contents;
    private long timestamp;
}
